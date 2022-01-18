package datastream

import (
	"log"
	"math"
)

type MarshalException struct {
	msg string
}

func (m *MarshalException) Error() string {
	panic(m.msg)
}

func (m *MarshalException) RuntimeError() {
	panic(m.msg)
}

func Max(x, y int) int {
	if x < y {
		return x
	}
	return y
}

func Min(x, y int) int {
	if x < y {
		return x
	}
	return y
}

type Octets struct {
	data     []byte
	beginPos int
	endPos   int
	capacity int
}

func NewOctetsByCapacity(capacity int) *Octets {
	return &Octets{data: make([]byte, capacity), beginPos: 0, endPos: 0, capacity: capacity}
}

func NewOctetsByData(bytes []byte) *Octets {
	return &Octets{data: bytes[:], beginPos: 0, endPos: len(bytes), capacity: len(bytes)}
}

func NewOctetsByDataAndIndex(bytes []byte, readIndex int, writeIndex int) *Octets {
	return &Octets{data: bytes[:], beginPos: readIndex, endPos: writeIndex, capacity: len(bytes)}
}

func (o *Octets) Replace(bytes []byte) {
	o.capacity = len(bytes)
	o.data = bytes[:]
	o.beginPos = 0
	o.endPos = o.capacity
}

func (o *Octets) Remaining() int {
	return o.endPos - o.beginPos
}

func (o *Octets) Clear() {
	o.beginPos = 0
	o.endPos = 0
}

func (o *Octets) SkipBytes() {
	n := int(o.ReadSize())
	o.SureRead(n)
	o.beginPos += n
}

func (o *Octets) SureRead(size int) {
	if o.beginPos+size > o.endPos {
		exception := MarshalException{"not enough data"}
		exception.RuntimeError()
	}
}

func (o *Octets) chooseNewSize(originSize int, needSize int) int {
	newSize := Max(originSize, 12)
	for newSize < needSize {
		newSize = newSize * 3 / 2
	}
	return newSize
}

func (o *Octets) SureWrite(size int) {
	if o.endPos+size > o.capacity {
		//不能直接往后写
		curSize := o.Remaining()
		needSize := curSize + size
		if needSize > o.capacity {
			o.capacity = o.chooseNewSize(o.capacity, needSize)
			newData := make([]byte, o.capacity)
			copy(newData, o.data[o.beginPos:o.endPos])
			o.data = newData
		} else {
			copy(o.data, o.data[o.beginPos:o.endPos])
		}
		o.beginPos = 0
		o.endPos = curSize
	}
}

func (o *Octets) AddWriteIndex(add int) {
	o.endPos += add
}

func (o *Octets) AddReadIndex(add int) {
	o.beginPos += add
}

func (o *Octets) Bytes() []byte {
	return o.data
}

func (o *Octets) ToArray() []byte {
	n := o.Remaining()
	if n > 0 {
		return o.data[o.beginPos:o.endPos]
	} else {
		return make([]byte, 0)
	}
}

func (o *Octets) CompactBuffer() {

}

func (o *Octets) ReadSize() uint32 {
	return o.ReadCompactUint32()
}

func (o *Octets) WriteSize(x uint32) {
	o.WriteCompactUint32(x)
}

func (o *Octets) ReadCompactShort() int16 {
	o.SureRead(1)
	h := int(o.data[o.beginPos])
	if h < 0x80 {
		o.beginPos++
		return int16(h)
	} else if h < 0xc0 {
		o.SureRead(2)
		x := (h&0x3f)<<8 | int(o.data[o.beginPos+1])
		o.beginPos += 2
		return int16(x)
	} else if h == 0xff {
		o.SureRead(3)
		x := int(o.data[o.beginPos+1])<<8 | int(o.data[o.beginPos+2])
		o.beginPos += 3
		return int16(x)
	} else {
		panic("exceed max short")
	}
}

func (o *Octets) WriteCompactShort(x int16) {
	if x > 0 {
		if x < 0x80 {
			o.SureWrite(1)
			o.data[o.endPos] = byte(x)
			o.endPos++
		} else if x < 0x4000 {
			//去除符号位的最高位
			o.SureWrite(2)
			o.data[o.endPos+1] = byte(x)
			o.data[o.endPos] = byte(x>>8 | 0x80)
			o.endPos += 2
			return
		}
	}
	o.SureWrite(3)
	o.data[o.endPos] = byte(0xff)
	o.data[o.endPos+2] = byte(x)
	o.data[o.endPos+1] = byte(x >> 8)
	o.endPos += 3
}

func (o *Octets) ReadCompactInt32() int32 {
	o.SureRead(1)
	h := int(o.data[o.beginPos])
	if h < 0x80 {
		o.beginPos++
		return int32(h)
	} else if h < 0xc0 {
		o.SureRead(2)
		x := ((h & 0x3f) << 8) | int(o.data[o.beginPos+1])
		o.beginPos += 2
		return int32(x)
	} else if h < 0xe0 {
		o.SureRead(3)
		x := (h&0x1f)<<16 | int(o.data[o.beginPos+1])<<8 | int(o.data[o.beginPos+2])
		o.beginPos += 3
		return int32(x)
	} else if h < 0xf0 {
		o.SureRead(4)
		x := (h&0x0f)<<24 | int(o.data[o.beginPos+1])<<16 | int(o.data[o.beginPos+2])<<8 | int(o.data[o.beginPos+3])
		o.beginPos += 4
		return int32(x)
	} else {
		o.SureRead(5)
		x := int(o.data[o.beginPos+1])<<24 | int(o.data[o.beginPos+2])<<16 | int(o.data[o.beginPos+3])<<8 | int(o.data[o.beginPos+4])
		o.beginPos += 5
		return int32(x)
	}
}

func (o *Octets) WriteCompactInt32(x int32) {
	if x >= 0 {
		if x < 0x80 {
			o.SureWrite(1)
			o.data[o.endPos] = byte(x)
			o.endPos++
			return
		} else if x < 0x4000 {
			o.SureWrite(2)
			o.data[o.endPos+1] = byte(x)
			o.data[o.endPos] = byte((x >> 8) | 0x80)
			o.endPos += 2
			return
		} else if x < 0x200000 {
			o.SureWrite(3)
			o.data[o.endPos+2] = byte(x)
			o.data[o.endPos+1] = byte(x >> 8)
			o.data[o.endPos] = byte((x >> 16) | 0xc0)
			o.endPos += 3
			return
		} else if x < 0x10000000 {
			o.SureWrite(4)
			o.data[o.endPos+3] = byte(x)
			o.data[o.endPos+2] = byte(x >> 8)
			o.data[o.endPos+1] = byte(x >> 16)
			o.data[o.endPos] = byte((x >> 24) | 0xe0)
			o.endPos += 4
			return
		}
	}
	o.SureWrite(5)
	o.data[o.endPos] = byte(0xf0)
	o.data[o.endPos+4] = byte(x)
	o.data[o.endPos+3] = byte(x >> 8)
	o.data[o.endPos+2] = byte(x >> 16)
	o.data[o.endPos+1] = byte(x >> 24)
	o.endPos += 5
}

func (o *Octets) ReadCompactInt64() int64 {
	o.SureRead(1)
	h := int(o.data[o.beginPos])
	if h < 0x80 {
		o.beginPos++
		return int64(h)
	} else if h < 0xc0 {
		o.SureRead(2)
		x := ((h & 0x3f) << 8) | int(o.data[o.beginPos+1])
		o.beginPos += 2
		return int64(x)
	} else if h < 0xe0 {
		o.SureRead(3)
		x := (h&0x1f)<<16 | int(o.data[o.beginPos+1])<<8 | int(o.data[o.beginPos+2])
		o.beginPos += 3
		return int64(x)
	} else if h < 0xf0 {
		o.SureRead(4)
		x := (h&0x0f)<<24 | int(o.data[o.beginPos+1])<<16 | int(o.data[o.beginPos+2])<<8 | int(o.data[o.beginPos+3])
		o.beginPos += 4
		return int64(x)
	} else if h < 0xf8 {
		o.SureRead(5)
		xL := int(o.data[o.beginPos+1])<<24 | int(o.data[o.beginPos+2])<<16 | int(o.data[o.beginPos+3])<<8 | int(o.data[o.beginPos+4])
		xH := h & 0x07
		o.beginPos += 5
		return int64(xH)<<32 | int64(xL)
	} else if h < 0xfc {
		o.SureRead(6)
		xL := int(o.data[o.beginPos+2])<<24 | int(o.data[o.beginPos+3])<<16 | int(o.data[o.beginPos+4])<<8 | int(o.data[o.beginPos+5])
		xH := (h & 0x03 << 8) | int(o.data[o.beginPos+1])
		o.beginPos += 6
		return int64(xH)<<32 | int64(xL)
	} else if h < 0xfe {
		o.SureRead(7)
		xL := int(o.data[o.beginPos+3])<<24 | int(o.data[o.beginPos+4])<<16 | int(o.data[o.beginPos+5])<<8 | int(o.data[o.beginPos+6])
		xH := (h & 0x01 << 16) | int(o.data[o.beginPos+1])<<8 | int(o.data[o.beginPos+2])
		o.beginPos += 7
		return int64(xH)<<32 | int64(xL)
	} else if h < 0xff {
		o.SureRead(8)
		xL := int(o.data[o.beginPos+4])<<24 | int(o.data[o.beginPos+5])<<16 | int(o.data[o.beginPos+6])<<8 | int(o.data[o.beginPos+7])
		xH := int(o.data[o.beginPos+1])<<16 | int(o.data[o.beginPos+2])<<8 | int(o.data[o.beginPos+3])
		o.beginPos += 8
		return int64(xH)<<32 | int64(xL)
	} else {
		o.SureRead(9)
		xL := int(o.data[o.beginPos+5])<<24 | int(o.data[o.beginPos+6])<<16 | int(o.data[o.beginPos+7])<<8 | int(o.data[o.beginPos+8])
		xH := int(o.data[o.beginPos+1])<<24 | int(o.data[o.beginPos+2])<<16 | int(o.data[o.beginPos+3])<<8 | int(o.data[o.beginPos+4])
		o.beginPos += 9
		return int64(xH)<<32 | int64(xL)
	}
}

func (o *Octets) WriteCompactInt64(x int64) {
	if x >= 0 {
		if x < 0x80 {
			o.SureWrite(1)
			o.data[o.endPos] = byte(x)
			o.endPos++
			return
		} else if x < 0x4000 {
			o.SureWrite(2)
			o.data[o.endPos+1] = byte(x)
			o.data[o.endPos] = byte((x >> 8) | 0x80)
			o.endPos += 2
			return
		} else if x < 0x200000 {
			o.SureWrite(3)
			o.data[o.endPos+2] = byte(x)
			o.data[o.endPos+1] = byte(x >> 8)
			o.data[o.endPos] = byte((x >> 16) | 0xc0)
			o.endPos += 3
			return
		} else if x < 0x10000000 {
			o.SureWrite(4)
			o.data[o.endPos+3] = byte(x)
			o.data[o.endPos+2] = byte(x >> 8)
			o.data[o.endPos+1] = byte(x >> 16)
			o.data[o.endPos] = byte((x >> 24) | 0xe0)
			o.endPos += 4
			return
		} else if x < 0x800000000 {
			o.SureWrite(5)
			o.data[o.endPos+4] = byte(x)
			o.data[o.endPos+3] = byte(x >> 8)
			o.data[o.endPos+2] = byte(x >> 16)
			o.data[o.endPos+1] = byte(x >> 24)
			o.data[o.endPos] = byte(x>>32 | 0xf0)
			o.endPos += 5
			return
		} else if x < 0x40000000000 {
			o.SureWrite(6)
			o.data[o.endPos+5] = byte(x)
			o.data[o.endPos+4] = byte(x >> 8)
			o.data[o.endPos+3] = byte(x >> 16)
			o.data[o.endPos+2] = byte(x >> 24)
			o.data[o.endPos+1] = byte(x >> 32)
			o.data[o.endPos] = byte(x>>40 | 0xf8)
			o.endPos += 6
			return
		} else if x < 0x200000000000 {
			o.SureWrite(7)
			o.data[o.endPos+6] = byte(x)
			o.data[o.endPos+5] = byte(x >> 8)
			o.data[o.endPos+4] = byte(x >> 16)
			o.data[o.endPos+3] = byte(x >> 24)
			o.data[o.endPos+2] = byte(x >> 32)
			o.data[o.endPos+1] = byte(x >> 40)
			o.data[o.endPos] = byte(x>>48 | 0xfc)
			o.endPos += 7
			return
		} else if x < 0x100000000000000 {
			o.SureWrite(8)
			o.data[o.endPos+7] = byte(x)
			o.data[o.endPos+6] = byte(x >> 8)
			o.data[o.endPos+5] = byte(x >> 16)
			o.data[o.endPos+4] = byte(x >> 24)
			o.data[o.endPos+3] = byte(x >> 32)
			o.data[o.endPos+2] = byte(x >> 40)
			o.data[o.endPos+1] = byte(x >> 48)
			o.data[o.endPos] = 0xfe
			o.endPos += 8
			return
		}
	}
	o.SureWrite(9)
	o.data[o.endPos+8] = byte(x)
	o.data[o.endPos+7] = byte(x >> 8)
	o.data[o.endPos+6] = byte(x >> 16)
	o.data[o.endPos+5] = byte(x >> 24)
	o.data[o.endPos+4] = byte(x >> 32)
	o.data[o.endPos+3] = byte(x >> 40)
	o.data[o.endPos+2] = byte(x >> 48)
	o.data[o.endPos+1] = byte(x >> 56)
	o.data[o.endPos] = 0xff
	o.endPos += 9
}

func (o *Octets) ReadCompactUint32() uint32 {
	x := o.ReadCompactInt32()
	if x >= 0 {
		return uint32(x)
	} else {
		panic("read uint32 error")
	}
}

func (o *Octets) WriteCompactUint32(x uint32) {
	o.WriteCompactInt32(int32(x))
}

func (o *Octets) ReadInt32() int32 {
	return o.ReadCompactInt32()
}

func (o *Octets) WriteInt32(x int32) {
	o.WriteCompactInt32(x)
}

func (o *Octets) ReadInt64() int64 {
	return o.ReadCompactInt64()
}

func (o *Octets) WriteInt64(x int64) {
	o.WriteCompactInt64(x)
}

func (o *Octets) ReadFixedInt32() int32 {
	o.SureRead(4)
	x := int(o.data[o.beginPos]) | int(o.data[o.beginPos+1])<<8 | int(o.data[o.beginPos+2])<<16 | int(o.data[o.beginPos+3])<<24
	o.beginPos += 4
	return int32(x)
}

func (o *Octets) WriteFixedInt32(x int32) {
	o.SureWrite(4)
	o.data[o.endPos] = byte(x)
	o.data[o.endPos+1] = byte(x >> 8)
	o.data[o.endPos+2] = byte(x >> 16)
	o.data[o.endPos+3] = byte(x >> 24)
	o.endPos += 4
}

func (o *Octets) ReadFixedInt64() int64 {
	o.SureWrite(8)
	x := int64(o.data[o.beginPos]) | int64(o.data[o.beginPos+1])<<8 | int64(o.data[o.beginPos+2])<<16 | int64(o.data[o.beginPos+3])<<24 | int64(o.data[o.beginPos+4])<<32 | int64(o.data[o.beginPos+5])<<40 | int64(o.data[o.beginPos+6])<<48 | int64(o.data[o.beginPos+7])<<56
	o.beginPos += 8
	return x
}

func (o *Octets) WriteFixedInt64(x int64) {
	o.SureWrite(8)
	o.data[o.endPos] = byte(x)
	o.data[o.endPos+1] = byte(x >> 8)
	o.data[o.endPos+2] = byte(x >> 16)
	o.data[o.endPos+3] = byte(x >> 24)
	o.data[o.endPos+4] = byte(x >> 32)
	o.data[o.endPos+5] = byte(x >> 40)
	o.data[o.endPos+6] = byte(x >> 48)
	o.data[o.endPos+7] = byte(x >> 56)
	o.endPos += 8
}

func (o *Octets) ReadFloat32() float32 {
	return math.Float32frombits(uint32(o.ReadFixedInt32()))
}

func (o *Octets) WriteFloat32(x float32) {
	o.WriteFixedInt32(int32(math.Float32bits(x)))
}

func (o *Octets) ReadFloat64() float64 {
	return math.Float64frombits(uint64(o.ReadFixedInt64()))
}

func (o *Octets) WriteFloat64(x float64) {
	o.WriteFixedInt64(int64(math.Float64bits(x)))
}

func (o *Octets) ReadString() string {
	n := int(o.ReadSize())
	if n > 0 {
		o.SureRead(n)
		temp := o.data[o.beginPos : o.beginPos+n]
		o.beginPos += n
		return string(temp)
	} else {
		return ""
	}
}

func (o *Octets) WriteString(x string) {
	if len(x) > 0 {
		temp := []byte(x)
		n := len(temp)
		o.WriteCompactUint32(uint32(n))
		o.SureWrite(n)
		copy(o.data[o.endPos:], temp)
		o.endPos += n
	} else {
		o.WriteSize(0)
	}
}

func (o *Octets) WriteBytes(x []byte) {
	n := len(x)
	if n > 0 {
		o.WriteCompactUint32(uint32(n))
		o.SureWrite(n)
		copy(o.data[o.endPos:], x)
		o.endPos += n
	} else {
		o.WriteSize(0)
	}
}

func (o *Octets) ReadBytes() []byte {
	n := int(o.ReadSize())
	if n > 0 {
		o.SureRead(n)
		result := o.data[o.beginPos : o.beginPos+n]
		o.beginPos += n
		return result
	} else {
		return make([]byte, 0)
	}
}

func (o *Octets) ReadBool() bool {
	o.SureRead(1)
	x := o.data[o.beginPos] != 0
	o.beginPos++
	return x
}

func (o *Octets) WriteBool(b bool) {
	o.SureWrite(1)
	if b {
		o.data[o.endPos] = 1
	} else {
		o.data[o.endPos] = 0
	}
	o.endPos++
}

func main() {
	test := NewOctetsByCapacity(200)
	test.WriteInt32(199)
	log.Println(test.ReadInt32())
	test.WriteInt32(-18956677)
	log.Println(test.ReadInt32())
	test.WriteFixedInt32(200)
	log.Println(test.ReadFixedInt32())
	test.WriteInt64(300)
	log.Println(test.ReadInt64())
	test.WriteFixedInt64(454866832345)
	log.Println(test.ReadFixedInt64())
	test.WriteFloat32(32344321.2)
	log.Println(test.ReadFloat32())
	test.WriteFloat32(-30494566.1)
	log.Println(test.ReadFloat32())
	test.WriteString("xiongjie qing")
	log.Println(test.ReadString())
	test.WriteString("hahaerfokletog")
	log.Println(test.ReadString())
}
