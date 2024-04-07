#pragma once
namespace cfg
{
	class FOctets
	{
	public:
		static const TArray<uint8> Empty_Bytes;
	private:
		TArray<uint8> Data;
		int32 BeginPos = 0;
		int32 EndPos = 0;
	public:
		// Constructors
		FOctets()
		{
		}

		FOctets(int32 InitCapacity)
		{
			Data.Reserve(InitCapacity);
		}

		FOctets(const TArray<uint8>& Bytes)
		{
			this->Data = Bytes;
			this->EndPos = Bytes.Num();
		}


		FOctets(const TArray<uint8>& Bytes, const int32 BeginPos, const int32 Len)
		{
			this->Data = TArray(&Bytes[BeginPos], Len);
			this->EndPos = Len;
		}

		void WriteByte(uint8 Byte)
		{
			EnsureWrite(1);
			Data.Add(Byte);
			EndPos++;
		}

		int32 Remain()
		{
			return EndPos - BeginPos;
		}

		uint8 ReadByte()
		{
			EnsureRead(1);
			return Data[BeginPos++];
		}

		int16 ReadCompactShort()
		{
			EnsureRead(1);
			int32 h = Data[BeginPos] & 0xff;
			if (h < 0x80)
			{
				BeginPos++;
				return static_cast<int16>(h);
			}
			else if (h < 0xc0)
			{
				EnsureRead(2);
				int16 x = static_cast<int16>(((h & 0x3f) << 8) | (Data[BeginPos + 1] & 0xff));
				BeginPos += 2;
				return x;
			}
			else if (h == 0xff)
			{
				EnsureRead(3);
				int16 x = static_cast<int16>(((Data[BeginPos + 1] & 0xff) << 8) | (Data[BeginPos + 2] & 0xff));
				BeginPos += 3;
				return x;
			}
			else
			{
				throw FString(TEXT("exceed max short"));
			}
		}

		void WriteCompactShort(int16 Value)
		{
			if (Value >= 0)
			{
				if (Value < 0x80)
				{
					EnsureWrite(1);
					Data.Add(static_cast<uint8>(Value));
					EndPos++;
					return;
				}
				else if (Value < 0x4000)
				{
					EnsureWrite(2);
					Data.Add(static_cast<uint8>((Value >> 8) | 0x80));
					Data.Add(static_cast<uint8>(Value & 0xFF));
					EndPos += 2;
					return;
				}
			}
			EnsureWrite(3);
			Data.Add(0xFF);
			Data.Add(static_cast<uint8>(Value >> 8));
			Data.Add(static_cast<uint8>(Value & 0xFF));
			EndPos += 3;
		}

		int32 ReadCompactUint()
		{
			int32 n = ReadCompactInt();
			if (n >= 0)
			{
				return n;
			}
			throw FString(TEXT("exceed max uint"));
		}

		void WriteCompactUint32(int32 x)
		{
			WriteCompactInt(x);
		}

		int32 ReadInt()
		{
			return ReadCompactInt();
		}

		void WriteInt(int32 x)
		{
			WriteCompactInt(x);
		}

		int32 ReadSize()
		{
			return ReadCompactUint();
		}

		void WriteSize(int32 x)
		{
			WriteCompactUint32(x);
		}

		void WriteShort(int16 x)
		{
			WriteCompactShort(x);
		}

		int16 ReadShort()
		{
			return ReadCompactShort();
		}

		int32 ReadCompactInt()
		{
			EnsureRead(1);
			int32 h = Data[BeginPos] & 0xFF;
			if (h < 0x80)
			{
				BeginPos++;
				return h;
			}
			else if (h < 0xC0)
			{
				EnsureRead(2);
				int32 x = ((h & 0x3F) << 8) | (Data[BeginPos + 1] & 0xFF);
				BeginPos += 2;
				return x;
			}
			else if (h < 0xE0)
			{
				EnsureRead(3);
				int32 x = ((h & 0x1F) << 16) | ((Data[BeginPos + 1] & 0xFF) << 8) | (Data[BeginPos + 2] & 0xFF);
				BeginPos += 3;
				return x;
			}
			else if (h < 0xF0)
			{
				EnsureRead(4);
				int32 x = ((h & 0x0F) << 24) | ((Data[BeginPos + 1] & 0xFF) << 16) | ((Data[BeginPos + 2] & 0xFF) << 8)
					| (
						Data[BeginPos + 3] & 0xFF);
				BeginPos += 4;
				return x;
			}
			else
			{
				EnsureRead(5);
				int32 x = ((Data[BeginPos + 1] & 0xFF) << 24) | ((Data[BeginPos + 2] & 0xFF) << 16) | ((Data[BeginPos +
						3] &
					0xFF) << 8) | (Data[BeginPos + 4] & 0xFF);
				BeginPos += 5;
				return x;
			}
		}

		void WriteCompactInt(int32 Value)
		{
			if (Value >= 0)
			{
				if (Value < 0x80)
				{
					EnsureWrite(1);
					Data.Add(static_cast<uint8>(Value));
					EndPos++;
					return;
				}
				else if (Value < 0x4000)
				{
					EnsureWrite(2);
					Data.Add(static_cast<uint8>((Value >> 8) | 0x80));
					Data.Add(static_cast<uint8>(Value));
					EndPos += 2;
					return;
				}
				else if (Value < 0x200000)
				{
					EnsureWrite(3);
					Data.Add(static_cast<uint8>((Value >> 16) | 0xC0));
					Data.Add(static_cast<uint8>(Value >> 8));
					Data.Add(static_cast<uint8>(Value));
					EndPos += 3;
					return;
				}
				else if (Value < 0x10000000)
				{
					EnsureWrite(4);
					Data.Add(static_cast<uint8>((Value >> 24) | 0xE0));
					Data.Add(static_cast<uint8>(Value >> 16));
					Data.Add(static_cast<uint8>(Value >> 8));
					Data.Add(static_cast<uint8>(Value));
					EndPos += 4;
					return;
				}
			}
			EnsureWrite(5);
			Data.Add(static_cast<uint8>(0xF0));
			Data.Add(static_cast<uint8>(Value >> 24));
			Data.Add(static_cast<uint8>(Value >> 16));
			Data.Add(static_cast<uint8>(Value >> 8));
			Data.Add(static_cast<uint8>(Value));
			EndPos += 5;
		}

		int64 ReadCompactInt64()
		{
			EnsureRead(1);
			int32 h = Data[BeginPos] & 0xff;
			if (h < 0x80)
			{
				BeginPos++;
				return h;
			}
			else if (h < 0xc0)
			{
				EnsureRead(2);
				int64 x = ((h & 0x3f) << 8) | (Data[BeginPos + 1] & 0xff);
				BeginPos += 2;
				return x;
			}
			else if (h < 0xe0)
			{
				EnsureRead(3);
				int64 x = ((h & 0x1f) << 16) | ((Data[BeginPos + 1] & 0xff) << 8) | (Data[BeginPos + 2] & 0xff);
				BeginPos += 3;
				return x;
			}
			else if (h < 0xf0)
			{
				EnsureRead(4);
				int64 x = ((h & 0x0f) << 24) | ((Data[BeginPos + 1] & 0xff) << 16) | ((Data[BeginPos + 2] & 0xff) << 8)
					| (
						Data[BeginPos + 3] & 0xff);
				BeginPos += 4;
				return x;
			}
			else if (h < 0xf8)
			{
				EnsureRead(5);
				int64 x = ((int64)(h & 0x07) << 32) | ((Data[BeginPos + 1] & 0xff) << 24) | ((Data[BeginPos + 2] & 0xff)
					<<
					16) | ((Data[BeginPos + 3] & 0xff) << 8) | (Data[BeginPos + 4] & 0xff);
				BeginPos += 5;
				return x;
			}
			else if (h < 0xfc)
			{
				EnsureRead(6);
				int64 x = ((int64)((h & 0x03) << 8) | (Data[BeginPos + 1] & 0xff)) << 32 | ((Data[BeginPos + 2] & 0xff)
					<<
					24) | ((Data[BeginPos + 3] & 0xff) << 16) | ((Data[BeginPos + 4] & 0xff) << 8) | (Data[BeginPos + 5]
					&
					0xff);
				BeginPos += 6;
				return x;
			}
			else if (h < 0xfe)
			{
				EnsureRead(7);
				int64 x = ((int64)((h & 0x01) << 16) | ((Data[BeginPos + 1] & 0xff) << 8) | (Data[BeginPos + 2] & 0xff))
					<<
					32 | ((Data[BeginPos + 3] & 0xff) << 24) | ((Data[BeginPos + 4] & 0xff) << 16) | ((Data[BeginPos +
							5] &
						0xff) << 8) | (Data[BeginPos + 6] & 0xff);
				BeginPos += 7;
				return x;
			}
			else if (h < 0xff)
			{
				EnsureRead(8);
				int64 x = ((int64)(Data[BeginPos + 1] & 0xff) << 16) | ((Data[BeginPos + 2] & 0xff) << 8) | (Data[
						BeginPos +
						3]
					& 0xff);
				x <<= 32;
				x |= ((Data[BeginPos + 4] & 0xff) << 24) | ((Data[BeginPos + 5] & 0xff) << 16) | ((Data[BeginPos + 6] &
					0xff) << 8) | (Data[BeginPos + 7] & 0xff);
				BeginPos += 8;
				return x;
			}
			else
			{
				EnsureRead(9);
				int64 x = ((int64)(Data[BeginPos + 1] & 0xff) << 56) | ((int64)(Data[BeginPos + 2] & 0xff) << 48) | ((
							int64)
						(Data[BeginPos + 3] & 0xff) << 40) | ((int64)(Data[BeginPos + 4] & 0xff) << 32) |
					((Data[BeginPos + 5] & 0xff) << 24) | ((Data[BeginPos + 6] & 0xff) << 16) | ((Data[BeginPos + 7] &
							0xff)
						<< 8) | (Data[BeginPos + 8] & 0xff);
				BeginPos += 9;
				return x;
			}
		}

		void WriteCompactInt64(int64 Value)
		{
			if (Value >= 0)
			{
				if (Value < 0x80)
				{
					EnsureWrite(1);
					Data.Add((uint8)Value);
					EndPos++;
					return;
				}
				else if (Value < 0x4000)
				{
					EnsureWrite(2);
					Data.Add((uint8)((Value >> 8) | 0x80));
					Data.Add((uint8)Value);
					EndPos += 2;
					return;
				}
				else if (Value < 0x200000)
				{
					EnsureWrite(3);
					Data.Add((uint8)((Value >> 16) | 0xC0));
					Data.Add((uint8)(Value >> 8));
					Data.Add((uint8)Value);
					EndPos += 3;
					return;
				}
				else if (Value < 0x10000000)
				{
					EnsureWrite(4);
					Data.Add((uint8)((Value >> 24) | 0xE0));
					Data.Add((uint8)(Value >> 16));
					Data.Add((uint8)(Value >> 8));
					Data.Add((uint8)Value);
					EndPos += 4;
					return;
				}
				else if (Value < 0x800000000L)
				{
					EnsureWrite(5);
					Data.Add((uint8)((Value >> 32) | 0xF0));
					Data.Add((uint8)(Value >> 24));
					Data.Add((uint8)(Value >> 16));
					Data.Add((uint8)(Value >> 8));
					Data.Add((uint8)Value);
					EndPos += 5;
					return;
				}
				else if (Value < 0x40000000000L)
				{
					EnsureWrite(6);
					Data.Add((uint8)((Value >> 40) | 0xF8));
					Data.Add((uint8)(Value >> 32));
					Data.Add((uint8)(Value >> 24));
					Data.Add((uint8)(Value >> 16));
					Data.Add((uint8)(Value >> 8));
					Data.Add((uint8)Value);
					EndPos += 6;
					return;
				}
				else if (Value < 0x200000000000L)
				{
					EnsureWrite(7);
					Data.Add((uint8)((Value >> 48) | 0xFC));
					Data.Add((uint8)(Value >> 40));
					Data.Add((uint8)(Value >> 32));
					Data.Add((uint8)(Value >> 24));
					Data.Add((uint8)(Value >> 16));
					Data.Add((uint8)(Value >> 8));
					Data.Add((uint8)Value);
					EndPos += 7;
					return;
				}
				else if (Value < 0x100000000000000L)
				{
					EnsureWrite(8);
					Data.Add((uint8)0xFE);
					Data.Add((uint8)(Value >> 48));
					Data.Add((uint8)(Value >> 40));
					Data.Add((uint8)(Value >> 32));
					Data.Add((uint8)(Value >> 24));
					Data.Add((uint8)(Value >> 16));
					Data.Add((uint8)(Value >> 8));
					Data.Add((uint8)Value);
					EndPos += 8;
					return;
				}
			}
			EnsureWrite(9);
			Data.Add((uint8)0xFF);
			Data.Add((uint8)(Value >> 56));
			Data.Add((uint8)(Value >> 48));
			Data.Add((uint8)(Value >> 40));
			Data.Add((uint8)(Value >> 32));
			Data.Add((uint8)(Value >> 24));
			Data.Add((uint8)(Value >> 16));
			Data.Add((uint8)(Value >> 8));
			Data.Add((uint8)Value);
			EndPos += 9;
		}

		int64 ReadInt64()
		{
			return ReadCompactInt64();
		}

		void WriteInt64(int64 x)
		{
			WriteCompactInt64(x);
		}

		int32 ReadFixedInt()
		{
			EnsureRead(4);
			int32 x = (Data[BeginPos]) | ((Data[BeginPos + 1] & 0xff) << 8) | ((Data[BeginPos + 2] & 0xff) << 16) |
				((Data[BeginPos + 3] & 0xff) << 24);
			BeginPos += 4;
			return x;
		}

		void WriteFixedInt(int32 x)
		{
			// 小端序
			EnsureWrite(4);
			Data.Add(static_cast<uint8>(x));
			Data.Add(static_cast<uint8>(x >> 8));
			Data.Add(static_cast<uint8>(x >> 16));
			Data.Add(static_cast<uint8>(x >> 24));
			EndPos += 4;
		}

		int64 ReadFixedInt64()
		{
			EnsureRead(8);
			int64 x = (Data[BeginPos + 7] & 0xffLL) << 56 |
				(Data[BeginPos + 6] & 0xffLL) << 48 |
				(Data[BeginPos + 5] & 0xffLL) << 40 |
				(Data[BeginPos + 4] & 0xffLL) << 32 |
				((Data[BeginPos + 3] & 0xffLL) << 24) |
				((Data[BeginPos + 2] & 0xffLL) << 16) |
				((Data[BeginPos + 1] & 0xffLL) << 8) |
				(Data[BeginPos] & 0xff);
			BeginPos += 8;
			return x;
		}

		void WriteFixedInt64(int64 x)
		{
			EnsureWrite(8);
			Data.Add(static_cast<uint8>(x));
			Data.Add(static_cast<uint8>(x >> 8));
			Data.Add(static_cast<uint8>(x >> 16));
			Data.Add(static_cast<uint8>(x >> 24));
			Data.Add(static_cast<uint8>(x >> 32));
			Data.Add(static_cast<uint8>(x >> 40));
			Data.Add(static_cast<uint8>(x >> 48));
			Data.Add(static_cast<uint8>(x >> 56));
			EndPos += 8;
		}

		float ReadFloat()
		{
			union
			{
				int32 i;
				float f;
			} u;
			u.i = ReadFixedInt();
			return u.f;
		}

		void WriteFloat(float Value)
		{
			int32 AsInt;
			FMemory::Memcpy(&AsInt, &Value, sizeof(Value));
			WriteFixedInt(AsInt);
		}

		double ReadDouble()
		{
			union
			{
				int64 i;
				double d;
			} u;
			u.i = ReadFixedInt64();
			return u.d;
		}

		void WriteDouble(double Value)
		{
			int64 AsInt;
			FMemory::Memcpy(&AsInt, &Value, sizeof(Value));
			WriteFixedInt64(AsInt);
		}

		// FString ReadString()
		// {
		// 	int32 Len = ReadSize();
		// 	if (Len > 0)
		// 	{
		// 		EnsureRead(Len);
		// 		// 注意：这里Len指的是字节长度，需要转换为TCHAR数组的大小
		// 		FString Str(reinterpret_cast<const TCHAR*>(&Data[BeginPos]), Len / sizeof(TCHAR));
		// 		BeginPos += Len;
		// 		return Str;
		// 	}
		// 	return TEXT("");
		// }
		FString ReadString()
		{
			int32 Len = ReadSize(); // 读取UTF-8字符串的字节长度
			if (Len > 0)
			{
				EnsureRead(Len); // 确保有足够的数据可以读取

				// 创建一个临时数组来存储读取的UTF-8编码的字节数据
				// 并确保这些数据以null字节结尾
				TArray<uint8> Utf8Data;
				Utf8Data.Reserve(Len + 1);
				for (int32 i = 0; i < Len; ++i)
				{
					Utf8Data.Add(Data[BeginPos + i]);
				}
				// 添加null终止符确保字符串正确终止
				Utf8Data.Add(0);

				// 从UTF-8转换为TCHAR格式的FString
				FString Str = FString(UTF8_TO_TCHAR(Utf8Data.GetData()));

				BeginPos += Len; // 更新读取位置

				return Str;
			}

			return TEXT(""); // 如果长度为0，返回空字符串
		}

		void WriteString(const FString& Str)
		{
			// 将FString转换为UTF-8编码的ANSI字符串
			FTCHARToUTF8 Converted(*Str);
			const int32 Len = Converted.Length(); // 获取UTF-8字符串的字节长度（不包含null终止符）

			WriteSize(Len); // 首先写入字符串长度

			if (Len > 0)
			{
				EnsureWrite(Len); // 确保有足够的空间写入字符串数据

				// 将UTF-8编码的字符串数据添加到Data数组
				for (int32 i = 0; i < Len; ++i)
				{
					Data.Add((uint8)Converted.Get()[i]);
				}

				EndPos += Len; // 更新写入位置
			}
		}

		// void WriteString(FString& Str)
		// {
		// 	// 获取UTF-16编码的字符串长度（包括终结符）
		// 	int32 Len = FCString::Strlen(Str.GetCharArray().GetData()) * sizeof(TCHAR);
		// 	WriteSize(Len);
		// 	if (Len > 0)
		// 	{
		// 		EnsureWrite(Len);
		// 		for (int32 i = 0; i < Len / sizeof(TCHAR); i++)
		// 		{
		// 			// 直接写入字符数组的字节数据
		// 			uint8* CharBytes = reinterpret_cast<uint8*>(&Str[i]);
		// 			for (int32 ByteIndex = 0; ByteIndex < sizeof(TCHAR); ByteIndex++)
		// 			{
		// 				Data.Add(CharBytes[ByteIndex]);
		// 			}
		// 		}
		// 		EndPos += Len;
		// 	}
		// }

		TArray<uint8> ReadBytes()
		{
			int32 Len = ReadSize();
			if (Len > 0)
			{
				EnsureRead(Len);
				TArray<uint8> Bytes;
				Bytes.Append(&Data[BeginPos], Len);
				BeginPos += Len;
				return Bytes;
			}
			return Empty_Bytes;
		}

		void WriteByte(TArray<uint8>& x)
		{
			int32 Len = x.Num();
			WriteSize(Len);
			if (Len > 0)
			{
				EnsureWrite(Len);
				Data.Append(x);
				EndPos += Len;
			}
		}

		bool ReadBool()
		{
			EnsureRead(1);
			return Data[BeginPos++] != 0;
		}

		void WriteBool(bool x)
		{
			EnsureWrite(1);
			Data.Add(x ? 1 : 0);
		}


	private:
		void EnsureWrite(int32 n)
		{
			if (Data.Max() < (EndPos + n))
			{
				Data.Reserve(ChooseNewSize(Data.Max(), EndPos + n));
			}
		}

		void EnsureRead(int32 n)
		{
			if (BeginPos + n > EndPos)
			{
				throw FString(TEXT("Read operation exceeds buffer size"));
			}
		}

		int32 ChooseNewSize(int32 CurrentCapacity, int32 RequiredCapacity) const
		{
			int32 NewCapacity = FMath::Max(12, CurrentCapacity);

			// 如果新容量小于所需容量，按照一定的比例扩大新容量（这里使用1.5倍扩容策略）
			while (NewCapacity < RequiredCapacity)
			{
				NewCapacity = FMath::CeilToInt(NewCapacity * 1.5f);
			}

			return NewCapacity;
		}
	};
}
