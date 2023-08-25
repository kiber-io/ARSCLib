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
package com.reandroid.dex.ins;

import com.reandroid.arsc.item.IntegerItem;
import com.reandroid.arsc.item.ShortItem;
import com.reandroid.dex.base.DexBlockAlign;
import com.reandroid.dex.base.NumberArray;
import com.reandroid.dex.writer.SmaliWriter;
import com.reandroid.utils.HexUtil;

import java.io.IOException;

public class InsArray extends PayloadData {

    private final ShortItem elementWidth;
    private final IntegerItem elementCount;
    private final NumberArray numberArray;
    private final DexBlockAlign blockAlign;

    public InsArray() {
        super(4, Opcode.ARRAY_PAYLOAD);
        this.elementWidth = new ShortItem();
        this.elementCount = new IntegerItem();
        this.numberArray = new NumberArray(elementWidth, elementCount);
        this.blockAlign = new DexBlockAlign(numberArray);
        this.blockAlign.setAlignment(2);

        addChild(1, elementWidth);
        addChild(2, elementCount);
        addChild(3, numberArray);
        addChild(4, blockAlign);
    }

    @Override
    void appendCode(SmaliWriter writer) throws IOException {
        writer.newLine();
        writer.append('.');
        writer.append(getOpcode().getName());
        writer.append(' ');
        writer.append(numberArray.getWidth());
        writer.indentPlus();
        appendData(writer);
        writer.indentMinus();
        writer.newLine();
        writer.append(".end ");
        writer.append(getOpcode().getName());
    }
    private void appendData(SmaliWriter writer) throws IOException {
        int width = numberArray.getWidth();
        if(width == 1){
            buildHex(writer, width, numberArray.getByteUnsignedArray());
        }else if(width == 2){
            buildHex(writer, width, numberArray.getShortUnsignedArray());
        }else if(width == 4){
            buildHex(writer, width, numberArray.getIntArray());
        }else if(width == 8){
            buildHex(writer, width, numberArray.getLongArray());
        }
    }
    private void buildHex(SmaliWriter builder, int width, int[] numbers) throws IOException {
        for(int num : numbers){
            builder.newLine();
            builder.append(HexUtil.toHex(num, 1));
        }
    }
    private void buildHex(SmaliWriter builder, int width, long[] numbers) throws IOException {
        for(long num : numbers){
            builder.newLine();
            builder.append(HexUtil.toHex(num, 1));
        }
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getOpcode());
        String separator = "\n    ";
        int width = numberArray.getWidth() * 2;
        if(width == 2){
            buildHex(builder, separator, width, numberArray.getByteUnsignedArray());
        }else if(width == 4){
            buildHex(builder, separator, width, numberArray.getShortUnsignedArray());
        }else if(width == 8){
            buildHex(builder, separator, width, numberArray.getIntArray());
        }else if(width == 16){
            buildHex(builder, separator, width, numberArray.getLongArray());
        }else {
            builder.append(" # Invalid number width: ").append(numberArray.getWidth());
        }
        return builder.toString();
    }
    private static void buildHex(StringBuilder builder, String separator, int width, int[] numbers){
        for(int num : numbers){
            builder.append(separator);
            builder.append(HexUtil.toHex(num, width));
        }
    }
    private static void buildHex(StringBuilder builder, String separator, int width, long[] numbers){
        for(long num : numbers){
            builder.append(separator);
            builder.append(HexUtil.toHex(num, width));
        }
    }
}