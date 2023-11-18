/**
 *
 * Copyright 2023 SUBSTRATE LABORATORY LLC <info@sublab.dev>
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *    http://www.apache.org/licenses/LICENSE-2.0

 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 * 
 */

package dev.sublab.substrate.utils

import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import java.math.BigInteger
import kotlin.reflect.KClass
import kotlin.reflect.full.createType

class InvalidSerializerType: Throwable()
private object BigIntegerSerializer : KSerializer<BigInteger> {

    override val descriptor = PrimitiveSerialDescriptor("java.math.BigInteger", PrimitiveKind.LONG)

    override fun deserialize(decoder: Decoder): BigInteger = when(decoder) {
        is JsonDecoder -> decoder.decodeJsonElement().jsonPrimitive.content.toBigInteger()
        else -> decoder.decodeString().toBigInteger()
    }

    override fun serialize(encoder: Encoder, value: BigInteger) = when(encoder) {
        is JsonEncoder -> encoder.encodeJsonElement(Json.parseToJsonElement(value.toString()))
        else           -> encoder.encodeString(value.toString())
    }
}

@Suppress("unchecked_cast")
fun <T: Any> serializerOrNull(type: KClass<T>?) = type?.let {
    when(it) {
        BigInteger::class -> BigIntegerSerializer as? KSerializer<T>
        else -> kotlinx.serialization.serializer(it.createType()) as? KSerializer<T>
    }
}

fun <T: Any> serializer(type: KClass<T>?) = serializerOrNull(type) ?: throw InvalidSerializerType()

