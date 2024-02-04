export class Octets {
    private data: Uint8Array
    private beginPos: number
    private endPos: number
    private capacity: number

    constructor(data: Uint8Array, beginPos: number, endPos: number) {
        this.data = data
        this.beginPos = beginPos
        this.endPos = endPos
        this.capacity = data.length
    }

    public replace(data: Uint8Array) {
        this.data = data
        this.capacity = data.length
        this.beginPos = 0
        this.endPos = this.capacity
    }

    public remaining(): number {
        return this.endPos - this.beginPos
    }

    public clear() {
        this.beginPos = 0
        this.endPos = 0
    }

    public sureRead(size: number) {
        if (this.beginPos + size > this.endPos) {
            throw new Error("not enough data")
        }
    }

    public chooseNewSize(originSize: number, needSize: number): number {
        let newSize = Math.max(originSize, 12)
        if (newSize < needSize) {
            newSize = newSize * 3 / 2
        }
        return newSize
    }

    public sureWrite(size: number) {
        if (this.endPos + size > this.capacity) {
            let curSize = this.remaining()
            let needSize = curSize + size
            if (needSize > this.capacity) {
                // 如果需要扩容
                this.capacity = this.chooseNewSize(this.capacity, needSize)
                let temp = this.data.slice(this.beginPos, this.endPos)
                this.data = new Uint8Array(temp,0, this.capacity)
            } else {
                this.data.copyWithin(0, this.beginPos, this.endPos)
            }
            this.beginPos = 0
            this.endPos = curSize
        }
    }

    public addWriteIndex(add: number) {
        this.endPos += add
    }

    public addReadIndex(add: number) {
        this.beginPos += add
    }

    public readSize(): number {
        return this.readCompactInt32()
    }

    public writeSize(x: number) {
        return this.writeCompactInt32(x)
    }

    private readUInt8(pos: number): number {
        return this.data[pos];
    }

    // Write a single byte as unsigned to the data.
    private writeUInt8(value: number, pos: number) {
        this.data[pos] = value;
    }

    // Read a 16-bit unsigned integer from the data in little-endian order.
    private readUInt16LE(pos: number): number {
        return this.data[pos] | (this.data[pos + 1] << 8);
    }

    // Write a 16-bit unsigned integer to the data in little-endian order.
    private writeUInt16LE(value: number, pos: number) {
        this.data[pos] = value & 0xff;
        this.data[pos + 1] = (value >> 8) & 0xff;
    }

    // Read a 32-bit unsigned integer from the data in little-endian order.
    private readUInt32LE(pos: number): number {
        return (
            this.data[pos] |
            (this.data[pos + 1] << 8) |
            (this.data[pos + 2] << 16) |
            (this.data[pos + 3] << 24) // Handle as signed integer
        ) >>> 0; // Convert to unsigned via unsigned right shift
    }

    // Write a 32-bit unsigned integer to the data in little-endian order.
    private writeUInt32LE(value: number, pos: number) {
        this.data[pos] = value & 0xff;
        this.data[pos + 1] = (value >> 8) & 0xff;
        this.data[pos + 2] = (value >> 16) & 0xff;
        this.data[pos + 3] = (value >> 24) & 0xff;
    }


    public readCompactShort(): number {
        this.sureRead(1)
        let h = this.readUInt8(this.beginPos)
        if (h < 0x80) {
            this.beginPos++
            return h
        } else if (h < 0xc0) {
            this.sureRead(2)
            let x = (h & 0x3f) << 8 | this.readUInt8(this.beginPos + 1)
            this.beginPos += 2
            return x
        } else if (h == 0xff) {
            this.sureRead(3)
            let x = this.readUInt8(this.beginPos + 1) << 8 | this.readUInt8(this.beginPos + 2)
            this.beginPos += 3
            return x
        } else {
            throw new Error("exceed max short")
        }
    }

    public writeCompactShort(x: number) {
        if (x > 0) {
            if (x < 0x80) {
                this.sureWrite(1)
                this.writeUInt8(x, this.endPos)
                this.endPos++
                return
            } else if (x < 0x4000) {
                this.sureWrite(2)
                this.writeUInt8(x & 0xff, this.endPos + 1)
                this.writeUInt8(x >> 8 | 0x80, this.endPos)
                this.endPos += 2
                return
            }
        }
        this.sureWrite(3)
        this.writeUInt8(0xff, this.endPos)
        this.writeUInt8(x >> 8, this.endPos + 1)
        this.writeUInt8(x & 0xff, this.endPos + 2)
        this.endPos += 3
    }

    public readCompactInt32(): number {
        this.sureRead(1)
        let h = this.readUInt8(this.beginPos)
        if (h < 0x80) {
            this.beginPos++
            return h
        } else if (h < 0xc0) {
            this.sureRead(2)
            let x = (h & 0x3f) << 8 | this.readUInt8(this.beginPos + 1)
            this.beginPos += 2
            return x
        } else if (h < 0xe0) {
            this.sureRead(3)
            let x = (h & 0x1f) << 16 | this.readUInt8(this.beginPos + 1) << 8 | this.readUInt8(this.beginPos + 2)
            this.beginPos += 3
            return x
        } else if (h < 0xf0) {
            this.sureRead(4)
            let x = (h & 0x0f) << 24 | this.readUInt8(this.beginPos + 1) << 16 | this.readUInt8(this.beginPos + 2) << 8 | this.readUInt8(this.beginPos + 3)
            this.beginPos += 4
            return x
        } else {
            this.sureRead(5)
            let x = this.readUInt8(this.beginPos + 1) << 24 | this.readUInt8(this.beginPos + 2) << 16 | this.readUInt8(this.beginPos + 3) << 8 | this.readUInt8(this.beginPos + 4)
            this.beginPos += 5
            return x
        }
    }

    public writeCompactInt32(x: number) {
        if (x >= 0) {
            if (x < 0x80) {
                this.sureWrite(1)
                this.writeUInt8(x, this.endPos)
                this.endPos++
                return
            } else if (x < 0x4000) {
                this.sureWrite(2)
                this.writeUInt8(x & 0xff, this.endPos + 1)
                this.writeUInt8((x >> 8) | 0x80, this.endPos)
                this.endPos += 2
                return
            } else if (x < 0x200000) {
                this.sureWrite(3)
                this.writeUInt8(x & 0xff, this.endPos + 2)
                this.writeUInt8((x >> 8) & 0xff, this.endPos + 1)
                this.writeUInt8((x >> 16) | 0xc0, this.endPos)
                this.endPos += 3
                return
            } else if (x < 0x10000000) {
                this.sureWrite(4)
                this.writeUInt8(x & 0xff, this.endPos + 3)
                this.writeUInt8((x >> 8) & 0xff, this.endPos + 2)
                this.writeUInt8((x >> 16) & 0xff, this.endPos + 1)
                this.writeUInt8((x >> 24) | 0xe0, this.endPos)
                this.endPos += 4
                return
            }
        }
        this.sureWrite(5)
        this.writeUInt8(0xf0, this.endPos)
        this.writeUInt8(x & 0xff, this.endPos + 4)
        this.writeUInt8(x >> 8 & 0xff, this.endPos + 3)
        this.writeUInt8(x >> 16 & 0xff, this.endPos + 2)
        this.writeUInt8(x >> 24 & 0xff, this.endPos + 1)
        this.endPos += 5
    }

    public readCompactInt64(): number {
        this.sureRead(1)
        let h = this.readUInt8(this.beginPos)
        if (h < 0x80) {
            this.beginPos++
            return h
        } else if (h < 0xc0) {
            this.sureRead(2)
            let x = (h & 0x3f) << 8 | this.readUInt8(this.beginPos + 1)
            this.beginPos += 2
            return x
        } else if (h < 0xe0) {
            this.sureRead(3)
            let x = (h & 0x1f) << 16 | this.readUInt8(this.beginPos + 1) << 8 | this.readUInt8(this.beginPos + 2)
            this.beginPos += 3
            return x
        } else if (h < 0xf0) {
            this.sureRead(4)
            let x = (h & 0x0f) << 24 | this.readUInt8(this.beginPos + 1) << 16 | this.readUInt8(this.beginPos + 2) << 8 | this.readUInt8(this.beginPos + 3)
            this.beginPos += 4
            return x
        } else if (h < 0xf8) {
            this.sureRead(5)
            let xL = this.readUInt8(this.beginPos + 1) << 24 | this.readUInt8(this.beginPos + 2) << 16 | this.readUInt8(this.beginPos + 3) << 8 | this.readUInt8(this.beginPos + 4)
            let xH = h & 0x07
            this.beginPos += 5
            return xH * Math.pow(2, 32) + (xL >>> 0)
        } else if (h < 0xfc) {
            this.sureRead(6)
            let xL = this.readUInt8(this.beginPos + 2) << 24 | this.readUInt8(this.beginPos + 3) << 16 | this.readUInt8(this.beginPos + 4) << 8 | this.readUInt8(this.beginPos + 5)
            let xH = (h & 0x03 << 8) | this.readUInt8(this.beginPos + 1)
            this.beginPos += 6
            return xH * Math.pow(2, 32) + (xL >>> 0)
        } else if (h < 0xfe) {
            this.sureRead(7)
            let xL = this.readUInt8(this.beginPos + 3) << 24 | this.readUInt8(this.beginPos + 4) << 16 | this.readUInt8(this.beginPos + 5) << 8 | this.readUInt8(this.beginPos + 6)
            let xH = (h & 0x01 << 16) | this.readUInt8(this.beginPos + 1) << 8 | this.readUInt8(this.beginPos + 2)
            this.beginPos += 7
            return xH * Math.pow(2, 32) + (xL >>> 0)
        } else if (h < 0xff) {
            this.sureRead(8)
            let xL = this.readUInt8(this.beginPos + 4) << 24 | this.readUInt8(this.beginPos + 5) << 16 | this.readUInt8(this.beginPos + 6) << 8 | this.readUInt8(this.beginPos + 7)
            let xH = this.readUInt8(this.beginPos + 1) << 16 | this.readUInt8(this.beginPos + 2) << 8 | this.readUInt8(this.beginPos + 3)
            this.beginPos += 8
            return xH * Math.pow(2, 32) + (xL >>> 0)
        } else {
            this.sureRead(9)
            let xL = this.readUInt8(this.beginPos + 5) << 24 | this.readUInt8(this.beginPos + 6) << 16 | this.readUInt8(this.beginPos + 7) << 8 | this.readUInt8(this.beginPos + 8)
            let xH = this.readUInt8(this.beginPos + 1) << 24 | this.readUInt8(this.beginPos + 2) << 16 | this.readUInt8(this.beginPos + 3) << 8 | this.readUInt8(this.beginPos + 4)
            this.beginPos += 9
            return xH * Math.pow(2, 32) + (xL >>> 0)
        }
    }

    public readInt32(): number {
        return this.readCompactInt32()
    }

    public writeInt32(x: number) {
        return this.writeCompactInt32(x)
    }

    public readInt64(): number {
        return this.readCompactInt64()
    }

    public readFixedInt32(): number {
        this.sureRead(4)
        let x = this.readUInt8(this.beginPos + 3) << 24 | this.readUInt8(this.beginPos + 2) << 16 | this.readUInt8(this.beginPos + 1) << 8 | this.readUInt8(this.beginPos)
        this.beginPos += 4
        return x
    }

    public writeFixedInt32(x: number) {
        this.sureWrite(4)
        this.writeUInt8(x & 0xff, this.endPos)
        this.writeUInt8(x >> 8 & 0xff, this.endPos + 1)
        this.writeUInt8(x >> 16 & 0xff, this.endPos + 2)
        this.writeUInt8(x >> 24 & 0xff, this.endPos + 3)
        this.endPos += 4
    }

    public readFixedInt64(): number {
        this.sureRead(8)
        const view = new DataView(this.data.buffer);
        let x = view.getBigInt64(this.beginPos, true);
        this.beginPos += 8
        return Number(x)
    }

    public writeFixedInt64(x: number) {
        this.sureWrite(8)
        const view = new DataView(this.data.buffer);
        view.setBigInt64(this.endPos, BigInt(x), true); // true for little-endian
        this.endPos += 8
    }

    public readFloat32(): number {
        this.sureRead(4)
        const view = new DataView(this.data.buffer);
        let x = view.getFloat32(this.beginPos, true);
        this.beginPos += 4
        return x
    }

    public writeFloat32(x: number) {
        this.sureWrite(4)
        const view = new DataView(this.data.buffer);
        view.setFloat32(this.endPos, x, true);
        this.endPos += 4
    }

    public readFloat64(): number {
        this.sureRead(8)
        const view = new DataView(this.data.buffer);
        let x = view.getFloat64(this.beginPos, true);
        this.beginPos += 8
        return x
    }

    public writeFloat64(x: number) {
        this.sureWrite(8)
        const view = new DataView(this.data.buffer);
        view.setFloat64(this.endPos, x, true);
        this.endPos += 8
    }

    public readString(): string {
        let n = this.readSize()
        if (n > 0) {
            this.sureRead(n)
            let temp = this.data.slice(this.beginPos, this.beginPos + n)
            this.beginPos += n
            return new TextDecoder().decode(temp)
        } else {
            return ""
        }
    }

    public writeString(x: string) {
        if (x.length > 0) {
            this.writeSize(x.length)
            const encoder = new TextEncoder();
            const encodedString = encoder.encode(x);
            this.sureWrite(encodedString.length);
            this.data.set(encodedString, this.endPos); // Copy the encoded string into the data
            this.endPos += encodedString.length;
        } else {
            this.writeSize(0)
        }
    }

    public readBool(): boolean {
        this.sureRead(1)
        let x = this.readUInt8(this.beginPos) != 0
        this.beginPos++
        return x
    }

    public writeBool(x: boolean) {
        this.sureWrite(1)
        if (x) {
            this.writeUInt8(1, this.endPos)
        } else {
            this.writeUInt8(0, this.endPos)
        }
        this.endPos++
    }

}

export function WrapOctets(data: Uint8Array): Octets {
    return new Octets(data, 0, data.length)
}

export function NewOctetsByCapacity(capacity: number): Octets {
    return new Octets(new Uint8Array(capacity), 0, 0)
}
