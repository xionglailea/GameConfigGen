using System;
using System.Runtime.InteropServices;
using System.Text;

namespace datastream
{
    internal class MarshalException : Exception
    {
        public MarshalException(string message) : base(message)
        {
        }

        public MarshalException()
        {
        }
    };

    internal class InvalidSizeException : Exception { }

    public sealed class Octets : ICloneable
    {
        private byte[] data;
        private int beginPos;
        private int endPos;
        private int capacity;


        public Octets() : this(Array.Empty<byte>())
        {

        }

        public object Clone()
        {
            return new Octets(this.ToArray());
        }

        public Octets(int capacity)
        {
            this.capacity = capacity;
            data = new byte[capacity];
            beginPos = 0;
            endPos = 0;
        }

        public Octets(byte[] bytes)
        {
            capacity = bytes.Length;
            this.data = bytes;
            beginPos = 0;
            endPos = capacity;
        }

        public Octets(byte[] bytes, int readIndex, int writeIndex)
        {
            capacity = bytes.Length;
            this.data = bytes;
            this.beginPos = readIndex;
            this.endPos = writeIndex;
        }

        public void Replace(byte[] bytes)
        {
            capacity = bytes.Length;
            this.data = bytes;
            beginPos = 0;
            endPos = capacity;
        }

        public int Size { get { return endPos - beginPos; } }
        public int Count { get { return endPos - beginPos; } }

        internal void SkipBytes()
        {
            int n = ReadSize();
            SureRead(n);
            beginPos += n;
        }

        public int ReadIndex { get { return beginPos; } }
        public int WriteIndex { get { return endPos; } }
        public void AddWriteIndex(int add)
        {
            endPos += add;
        }

        public void AddReadIndex(int add)
        {
            beginPos += add;
        }

        public byte[] Bytes { get { return data; } }

        public byte[] ToArray()
        {
            var n = Remaining;
            if (n > 0)
            {
                var arr = new byte[n];
                Buffer.BlockCopy(data, beginPos, arr, 0, n);
                return arr;
            }
            else
            {
                return Array.Empty<byte>();
            }
        }

        public int Remaining { get { return endPos - beginPos; } }
        public int Capacity { get { return capacity; } }

        public void CompactBuffer()
        {
            SureWrite(capacity + beginPos - WriteIndex);
        }

        public int NotCompactWritable { get { return capacity - endPos; } }

        public void WriteBytes(byte[] bs, int offset, int len)
        {
            SureWrite(len);
            Buffer.BlockCopy(bs, offset, data, endPos, len);
            endPos += len;
        }

        public void Clear()
        {
            beginPos = endPos = 0;
        }


        private int PropSize(int initSize, int needSize)
        {
            for (int i = Math.Max(initSize, 16); ; i <<= 1)
            {
                if (i >= needSize)
                    return i;
            }
        }

        public void SureWrite(int size)
        {
            if (endPos + size > capacity)
            {
                var needSize = endPos + size - beginPos;
                if (needSize < capacity)
                {
                    endPos -= ReadIndex;
                    Array.Copy(data, beginPos, data, 0, endPos);
                    beginPos = 0;
                }
                else
                {
                    capacity = PropSize(capacity, needSize);
                    var newBytes = new byte[capacity];
                    endPos -= beginPos;
                    Buffer.BlockCopy(data, ReadIndex, newBytes, 0, endPos);
                    beginPos = 0;
                    data = newBytes;
                }
            }
        }

        //    [MethodImpl(MethodImplOptions.AggressiveInlining)]
        private void SureRead(int size)
        {
            if (beginPos + size > endPos)
                throw new MarshalException();
        }

        public void Append(byte x)
        {
            SureWrite(1);
            data[endPos++] = x;
        }

        public void WriteBool(bool b)
        {
            SureWrite(1);
            data[endPos++] = (byte)(b ? 1 : 0);
        }

        public bool ReadBool()
        {
            SureRead(1);
            return data[beginPos++] != 0;
        }

        public void WriteByte(byte x)
        {
            SureWrite(1);
            data[endPos++] = x;
        }

        public byte ReadByte()
        {
            SureRead(1);
            return data[beginPos++];
        }


        public void WriteShort(short x)
        {
            if (x >= 0)
            {
                if (x < 0x80)
                {
                    SureWrite(1);
                    data[endPos++] = (byte)x;
                    return;
                }
                else if (x < 0x4000)
                {
                    SureWrite(2);
                    data[endPos + 1] = (byte)x;
                    data[endPos] = (byte)((x >> 8) | 0x80);
                    endPos += 2;
                    return;
                }
            }
            SureWrite(3);
            data[endPos] = (byte)0xff;
            data[endPos + 2] = (byte)x;
            data[endPos + 1] = (byte)(x >> 8);
            endPos += 3;
        }

        public short ReadShort()
        {
            SureRead(1);
            int h = (data[beginPos] & 0xff);
            if (h < 0x80)
            {
                beginPos++;
                return (short)h;
            }
            else if (h < 0xc0)
            {
                SureRead(2);
                int x = ((h & 0x3f) << 8) | (data[beginPos + 1] & 0xff);
                beginPos += 2;
                return (short)x;
            }
            else if ((h == 0xff))
            {
                SureRead(3);
                int x = ((data[beginPos + 1] & 0xff) << 8) | (data[beginPos + 2] & 0xff);
                beginPos += 3;
                return (short)x;
            }
            else
            {
                throw new MarshalException();
            }
        }

        // marshal int 
        // n -> (n << 1) ^ (n >> 31)
        // Read
        // (x >>> 1) ^ ((x << 31) >> 31)
        // (x >>> 1) ^ -(n&1)

        // marshal long
        // n -> (n << 1) ^ (n >> 63)
        // Read
        // (x >>> 1) ^((x << 63) >> 63)
        // (x >>> 1) ^ -(n&1L)

        public void WriteInt(int x)
        {
            WriteUint((uint)x);
        }

        public int ReadInt()
        {
            return (int)ReadUint();
        }


        private unsafe void WriteUint(uint x)
        {
            // 0 111 1111
            if (x < 0x80)
            {
                SureWrite(1);
                data[endPos++] = (byte)x;
            }
            else if (x < 0x4000) // 10 11 1111, -
            {
                SureWrite(2);
                data[endPos + 1] = (byte)x;
                data[endPos] = (byte)((x >> 8) | 0x80);
                endPos += 2;
            }
            else if (x < 0x200000) // 110 1 1111, -,-
            {
                SureWrite(3);
                data[endPos + 2] = (byte)x;
                data[endPos + 1] = (byte)(x >> 8);
                data[endPos] = (byte)((x >> 16) | 0xc0);
                endPos += 3;
            }
            else if (x < 0x10000000) // 1110 1111,-,-,-
            {
                SureWrite(4);
                data[endPos + 3] = (byte)x;
                data[endPos + 2] = (byte)(x >> 8);
                data[endPos + 1] = (byte)(x >> 16);
                data[endPos] = (byte)((x >> 24) | 0xe0);
                endPos += 4;
            }
            else
            {
                SureWrite(5);
                data[endPos] = 0xf0;
                data[endPos + 4] = (byte)x;
                data[endPos + 3] = (byte)(x >> 8);
                data[endPos + 2] = (byte)(x >> 16);
                data[endPos + 1] = (byte)(x >> 24);
                endPos += 5;
            }
        }

        public uint ReadUint()
        {
            SureRead(1);
            uint h = data[beginPos];
            if (h < 0x80)
            {
                beginPos++;
                return h;
            }
            else if (h < 0xc0)
            {
                SureRead(2);
                uint x = ((h & 0x3f) << 8) | data[beginPos + 1];
                beginPos += 2;
                return x;
            }
            else if (h < 0xe0)
            {
                SureRead(3);
                uint x = ((h & 0x1f) << 16) | ((uint)data[beginPos + 1] << 8) | data[beginPos + 2];
                beginPos += 3;
                return x;
            }
            else if (h < 0xf0)
            {

                SureRead(4);
                uint x = ((h & 0x0f) << 24) | ((uint)data[beginPos + 1] << 16) | ((uint)data[beginPos + 2] << 8) | data[beginPos + 3];
                beginPos += 4;
                return x;
            }
            else
            {
                SureRead(5);
                uint x = ((uint)data[beginPos + 1] << 24) | ((uint)(data[beginPos + 2] << 16)) | ((uint)data[beginPos + 3] << 8) | ((uint)data[beginPos + 4]);
                beginPos += 5;
                return x;
            }
        }


        public void WriteLong(long x)
        {
            WriteUlong((ulong)x);
        }

        public long ReadLong()
        {
            return (long)ReadUlong();
        }

        private void WriteUlong(ulong x)
        {
            // 0 111 1111
            if (x < 0x80)
            {
                SureWrite(1);
                data[endPos++] = (byte)x;
            }
            else if (x < 0x4000) // 10 11 1111, -
            {
                SureWrite(2);
                data[endPos + 1] = (byte)x;
                data[endPos] = (byte)((x >> 8) | 0x80);
                endPos += 2;
            }
            else if (x < 0x200000) // 110 1 1111, -,-
            {
                SureWrite(3);
                data[endPos + 2] = (byte)x;
                data[endPos + 1] = (byte)(x >> 8);
                data[endPos] = (byte)((x >> 16) | 0xc0);
                endPos += 3;
            }
            else if (x < 0x10000000) // 1110 1111,-,-,-
            {
                SureWrite(4);
                data[endPos + 3] = (byte)x;
                data[endPos + 2] = (byte)(x >> 8);
                data[endPos + 1] = (byte)(x >> 16);
                data[endPos] = (byte)((x >> 24) | 0xe0);
                endPos += 4;
            }
            else if (x < 0x800000000L) // 1111 0xxx,-,-,-,-
            {
                SureWrite(5);
                data[endPos + 4] = (byte)x;
                data[endPos + 3] = (byte)(x >> 8);
                data[endPos + 2] = (byte)(x >> 16);
                data[endPos + 1] = (byte)(x >> 24);
                data[endPos] = (byte)((x >> 32) | 0xf0);
                endPos += 5;
            }
            else if (x < 0x40000000000L) // 1111 10xx, 
            {
                SureWrite(6);
                data[endPos + 5] = (byte)x;
                data[endPos + 4] = (byte)(x >> 8);
                data[endPos + 3] = (byte)(x >> 16);
                data[endPos + 2] = (byte)(x >> 24);
                data[endPos + 1] = (byte)(x >> 32);
                data[endPos] = (byte)((x >> 40) | 0xf8);
                endPos += 6;
            }
            else if (x < 0x200000000000L) // 1111 110x,
            {
                SureWrite(7);
                data[endPos + 6] = (byte)x;
                data[endPos + 5] = (byte)(x >> 8);
                data[endPos + 4] = (byte)(x >> 16);
                data[endPos + 3] = (byte)(x >> 24);
                data[endPos + 2] = (byte)(x >> 32);
                data[endPos + 1] = (byte)(x >> 40);
                data[endPos] = (byte)((x >> 48) | 0xfc);
                endPos += 7;
            }
            else if (x < 0x100000000000000L) // 1111 1110
            {
                SureWrite(8);
                data[endPos + 7] = (byte)x;
                data[endPos + 6] = (byte)(x >> 8);
                data[endPos + 5] = (byte)(x >> 16);
                data[endPos + 4] = (byte)(x >> 24);
                data[endPos + 3] = (byte)(x >> 32);
                data[endPos + 2] = (byte)(x >> 40);
                data[endPos + 1] = (byte)(x >> 48);
                data[endPos] = 0xfe;
                endPos += 8;
            }
            else // 1111 1111
            {
                SureWrite(9);
                data[endPos] = 0xff;
                data[endPos + 8] = (byte)x;
                data[endPos + 7] = (byte)(x >> 8);
                data[endPos + 6] = (byte)(x >> 16);
                data[endPos + 5] = (byte)(x >> 24);
                data[endPos + 4] = (byte)(x >> 32);
                data[endPos + 3] = (byte)(x >> 40);
                data[endPos + 2] = (byte)(x >> 48);
                data[endPos + 1] = (byte)(x >> 56);
                endPos += 9;
            }
        }

        public ulong ReadUlong()
        {
            SureRead(1);
            uint h = data[beginPos];
            if (h < 0x80)
            {
                beginPos++;
                return h;
            }
            else if (h < 0xc0)
            {
                SureRead(2);
                uint x = ((h & 0x3f) << 8) | data[beginPos + 1];
                beginPos += 2;
                return x;
            }
            else if (h < 0xe0)
            {
                SureRead(3);
                uint x = ((h & 0x1f) << 16) | ((uint)data[beginPos + 1] << 8) | data[beginPos + 2];
                beginPos += 3;
                return x;
            }
            else if (h < 0xf0)
            {
                SureRead(4);
                uint x = ((h & 0x0f) << 24) | ((uint)data[beginPos + 1] << 16) | ((uint)data[beginPos + 2] << 8) | data[beginPos + 3];
                beginPos += 4;
                return x;
            }
            else if (h < 0xf8)
            {
                SureRead(5);
                uint xl = ((uint)data[beginPos + 1] << 24) | ((uint)(data[beginPos + 2] << 16)) | ((uint)data[beginPos + 3] << 8) | (data[beginPos + 4]);
                uint xh = h & 0x07;
                beginPos += 5;
                return ((ulong)xh << 32) | xl;
            }
            else if (h < 0xfc)
            {
                SureRead(6);
                uint xl = ((uint)data[beginPos + 2] << 24) | ((uint)(data[beginPos + 3] << 16)) | ((uint)data[beginPos + 4] << 8) | (data[beginPos + 5]);
                uint xh = ((h & 0x03) << 8) | data[beginPos + 1];
                beginPos += 6;
                return ((ulong)xh << 32) | xl;
            }
            else if (h < 0xfe)
            {
                SureRead(7);
                uint xl = ((uint)data[beginPos + 3] << 24) | ((uint)(data[beginPos + 4] << 16)) | ((uint)data[beginPos + 5] << 8) | (data[beginPos + 6]);
                uint xh = ((h & 0x01) << 16) | ((uint)data[beginPos + 1] << 8) | data[beginPos + 2];
                beginPos += 7;
                return ((ulong)xh << 32) | xl;
            }
            else if (h < 0xff)
            {
                SureRead(8);
                uint xl = ((uint)data[beginPos + 4] << 24) | ((uint)(data[beginPos + 5] << 16)) | ((uint)data[beginPos + 6] << 8) | (data[beginPos + 7]);
                uint xh = /*((h & 0x01) << 24) |*/ ((uint)data[beginPos + 1] << 16) | ((uint)data[beginPos + 2] << 8) | data[beginPos + 3];
                beginPos += 8;
                return ((ulong)xh << 32) | xl;
            }
            else
            {
                SureRead(9);
                uint xl = ((uint)data[beginPos + 5] << 24) | ((uint)(data[beginPos + 6] << 16)) | ((uint)data[beginPos + 7] << 8) | (data[beginPos + 8]);
                uint xh = ((uint)data[beginPos + 1] << 24) | ((uint)data[beginPos + 2] << 16) | ((uint)data[beginPos + 3] << 8) | data[beginPos + 4];
                beginPos += 9;
                return ((ulong)xh << 32) | xl;
            }
        }

        private unsafe static void Copy8(byte* dst, byte* src)
        {
            dst[0] = src[0];
            dst[1] = src[1];
            dst[2] = src[2];
            dst[3] = src[3];
            dst[4] = src[4];
            dst[5] = src[5];
            dst[6] = src[6];
            dst[7] = src[7];
        }

        private unsafe static void Copy4(byte* dst, byte* src)
        {
            dst[0] = src[0];
            dst[1] = src[1];
            dst[2] = src[2];
            dst[3] = src[3];
        }

        public unsafe int ReadFixedInt()
        {
            SureRead(4);
            if (!BitConverter.IsLittleEndian)
            {
                Array.Reverse(data, beginPos, 4);
            }
            int x;
            fixed (byte* b = &data[beginPos])
            {
                if ((long)b % 4 == 0)
                {
                    x = *(int*)b;
                }
                else
                {
                    Copy4((byte*)&x, b);
                }
            }
            //float x = BitConverter.ToSingle(bytes, readIndex);
            beginPos += 4;
            return x;
        }

        //const bool isLittleEndian = true;
        public unsafe void WriteFixedInt(int x)
        {
            SureWrite(4);

            fixed (byte* b = &data[endPos])
            {
                if ((long)b % 4 == 0)
                {
                    *(int*)b = x;
                }
                else
                {
                    Copy4(b, (byte*)&x);
                }
            }
            if (!BitConverter.IsLittleEndian)
            {
                Array.Reverse(data, endPos, 4);
            }
            endPos += 4;
        }


        public unsafe void WriteFixedLong(long x)
        {
            SureWrite(8);
            fixed (byte* b = &data[endPos])
            {
                if ((long)b % 8 == 0)
                {
                    *(long*)b = x;
                }
                else
                {
                    Copy8(b, (byte*)&x);
                }
            }
            if (!BitConverter.IsLittleEndian)
            {
                Array.Reverse(data, endPos, 8);
            }
            endPos += 8;
        }

        public unsafe long ReadFixedLong()
        {
            SureRead(8);
            if (!BitConverter.IsLittleEndian)
            {
                Array.Reverse(data, beginPos, 8);
            }
            long x;
            fixed (byte* b = &data[beginPos])
            {
                if ((long)b % 8 == 0)
                {
                    x = *(long*)b;
                }
                else
                {
                    Copy8((byte*)&x, b);
                }
            }
            //UnityEngine.Debug.LogFormat("unmarshal double. u:{0}", u);
            beginPos += 8;
            return x;
        }

        public unsafe float ReadFloat()
        {
            int x = ReadFixedInt();
            return *(float*)&x;
        }

        //const bool isLittleEndian = true;
        public unsafe void WriteFloat(float x)
        {
            WriteFixedInt(*(int*)&x);
        }

        public unsafe void WriteDouble(double x)
        {
            WriteFixedLong(*(long*)&x);
        }

        public unsafe double ReadDouble()
        {
            long x = ReadFixedLong();
            return *(double*)&x;
        }

        public void WriteSize(int n)
        {
            uint x = (uint)n;
            // 0 111 1111
            if (x < 0x80)
            {
                SureWrite(1);
                data[endPos++] = (byte)x;
            }
            else if (x < 0x4000) // 10 11 1111, -
            {
                SureWrite(2);
                data[endPos + 1] = (byte)x;
                data[endPos] = (byte)((x >> 8) | 0x80);
                endPos += 2;
            }
            else if (x < 0x200000) // 110 1 1111, -,-
            {
                SureWrite(3);
                data[endPos + 2] = (byte)x;
                data[endPos + 1] = (byte)(x >> 8);
                data[endPos] = (byte)((x >> 16) | 0xc0);
                endPos += 3;
            }
            else if (x < 0x10000000) // 1110 1111,-,-,-
            {
                SureWrite(4);
                data[endPos + 3] = (byte)x;
                data[endPos + 2] = (byte)(x >> 8);
                data[endPos + 1] = (byte)(x >> 16);
                data[endPos] = (byte)((x >> 24) | 0xe0);
                endPos += 4;
            }
            else
            {
                throw new MarshalException();
            }
        }

        public int ReadSize()
        {
            SureRead(1);
            int h = data[beginPos];
            if (h < 0x80)
            {
                beginPos++;
                return h;
            }
            else if (h < 0xc0)
            {
                SureRead(2);
                int x = ((h & 0x3f) << 8) | data[beginPos + 1];
                beginPos += 2;
                return x;
            }
            else if (h < 0xe0)
            {
                SureRead(3);
                int x = ((h & 0x1f) << 16) | (data[beginPos + 1] << 8) | data[beginPos + 2];
                beginPos += 3;
                return x;
            }
            else if (h < 0xf0)
            {

                SureRead(4);
                int x = ((h & 0x0f) << 24) | (data[beginPos + 1] << 16) | (data[beginPos + 2] << 8) | data[beginPos + 3];
                beginPos += 4;
                return x;
            }
            else
            {
                throw new InvalidSizeException();
            }
        }

        public void WriteSint(int x)
        {
            WriteUint(((uint)x << 1) | ((uint)x >> 31));
        }

        public int ReadSint()
        {
            int x = ReadInt();
            return ((int)((uint)x >> 1) | ((x & 1) << 31));
        }

        public void WriteSlong(long x)
        {
            WriteUlong(((ulong)x << 1) | ((ulong)x >> 63));
        }

        public long ReadSlong()
        {
            long x = ReadLong();
            return ((long)((ulong)x >> 1) | ((x & 1) << 63));
        }

        public void WriteString(string x)
        {
            var n = Encoding.UTF8.GetByteCount(x);
            WriteSize(n);
            if (n > 0)
            {
                SureWrite(n);
                Encoding.UTF8.GetBytes(x, 0, x.Length, data, endPos);
                endPos += n;
            }
        }

        // byte[], [start, end)
        public static Func<byte[], int, int, string> StringCacheFinder { get; set; }

        public string ReadString()
        {
            var n = ReadSize();
            if (n > 0)
            {
                SureRead(n);
                string s;
                if (StringCacheFinder == null)
                {
                    s = Encoding.UTF8.GetString(data, beginPos, n);
                }
                else
                {
                    s = StringCacheFinder(data, beginPos, n);
                }
                beginPos += n;
                return s;
            }
            else
            {
                return "";
            }
        }

        //[LuaInterface.LuaByteBuffer]
        public void WriteBytes(byte[] x)
        {
            var n = x.Length;
            WriteSize(n);
            SureWrite(n);
            x.CopyTo(data, endPos);
            endPos += n;
        }

        //[LuaInterface.LuaByteBuffer]
        public byte[] ReadBytes()
        {
            var n = ReadSize();
            if (n > 0)
            {
                SureRead(n);
                var x = new byte[n];
                Buffer.BlockCopy(data, beginPos, x, 0, n);
                beginPos += n;
                return x;
            }
            else
            {
                return Array.Empty<byte>();
            }
        }

        public class Msg
        {
            public int maxSize;
            public Octets body = new Octets();
        }

        public enum UnmarshalError
        {
            OK,
            NOT_ENOUGH,
            EXCEED_SIZE,
            UNMARSHAL_ERR,
        }

        public UnmarshalError TryUnmarshalBytes(Msg msg)
        {
            var oldReadIndex = beginPos;
            int n;
            try
            {
                n = ReadSize();
            }
            catch (InvalidSizeException)
            {
                throw;
            }
            catch (Exception)
            {
                //readIndex = oldReadIndex;
                return UnmarshalError.NOT_ENOUGH;
            }
            //UnityEngine.Debug.Log("unmarshal size:" + n);
            if (n > msg.maxSize)
                throw new MarshalException();
            if (Remaining < n)
            {
                beginPos = oldReadIndex;
                return UnmarshalError.NOT_ENOUGH;
            }

            // 每提取一个消息后直接送达反序列化
            // 故可以复用Octets
            // 确保使用的时候是立即反序列化
            var body = msg.body;
            body.data = data;
            body.beginPos = beginPos;
            beginPos += n;
            body.endPos = beginPos;

            return UnmarshalError.OK;
        }

        public void WriteOctets(Octets o)
        {
            var n = o.Remaining;
            WriteSize(n);
            WriteBytes(o.data, o.beginPos, n);
        }

        public Octets ReadOctets()
        {
            return new Octets(ReadBytes());
        }

        public override string ToString()
        {
            string[] datas = new string[endPos - ReadIndex];
            for (var i = beginPos; i < endPos; i++)
            {
                datas[i - beginPos] = data[i].ToString();
            }
            return string.Join(",", datas);
        }

        public static Octets FromJsonString(string value)
        {
            return FromString(value);
        }

        public static Octets FromString(string value)
        {
            if (string.IsNullOrEmpty(value))
            {
                return new Octets();
            }
            var ss = value.Split(",");
            byte[] data = new byte[ss.Length];
            for (int i = 0; i < data.Length; i++)
            {
                data[i] = byte.Parse(ss[i]);
            }
            return new Octets(data);
        }
    }
}
