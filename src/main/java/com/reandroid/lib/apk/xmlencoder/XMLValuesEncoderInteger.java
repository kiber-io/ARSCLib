 /*
  *  Copyright (C) 2022 github.com/REAndroid
  *
  *  Licensed under the Apache License, Version 2.0 (the "License");
  *  you may not use this file except in compliance with the License.
  *  You may obtain a copy of the License at
  *
  *      http://www.apache.org/licenses/LICENSE-2.0
  *
  * Unless required by applicable law or agreed to in writing, software
  * distributed under the License is distributed on an "AS IS" BASIS,
  * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
  * See the License for the specific language governing permissions and
  * limitations under the License.
  */
package com.reandroid.lib.apk.xmlencoder;

import com.reandroid.lib.arsc.decoder.ValueDecoder;
import com.reandroid.lib.arsc.value.EntryBlock;
import com.reandroid.lib.arsc.value.ValueType;

public class XMLValuesEncoderInteger extends XMLValuesEncoder{
    XMLValuesEncoderInteger(EncodeMaterials materials) {
        super(materials);
    }
    @Override
    void encodeStringValue(EntryBlock entryBlock, String value){
        value=value.trim();
        if(ValueDecoder.isInteger(value)){
            entryBlock.setValueAsRaw(ValueType.INT_DEC, ValueDecoder.parseInteger(value));
        }else if(ValueDecoder.isHex(value)){
            entryBlock.setValueAsRaw(ValueType.INT_HEX, ValueDecoder.parseHex(value));
        }else {
            throw new EncodeException("Unknown value for type <integer>: '"+value+"'");
        }
    }
}
