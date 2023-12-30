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
package com.reandroid.dex.key;

import com.reandroid.dex.id.FieldId;
import com.reandroid.utils.CompareUtil;
import com.reandroid.utils.StringsUtil;
import com.reandroid.utils.collection.CombiningIterator;
import com.reandroid.utils.collection.SingleIterator;

import java.util.Iterator;


public class FieldKey implements Key {

    private final String declaring;
    private final String name;
    private final String type;

    public FieldKey(String declaring, String name, String type) {
        this.declaring = declaring;
        this.name = name;
        this.type = type;
    }

    public FieldKey changeDefining(TypeKey typeKey){
        return changeDefining(typeKey.getTypeName());
    }
    public FieldKey changeDefining(String defining){
        if(defining.equals(getDeclaringName())){
            return this;
        }
        return new FieldKey(defining, getName(), getTypeName());
    }
    public FieldKey changeName(String name){
        if(name.equals(getName())){
            return this;
        }
        return new FieldKey(getDeclaringName(), name, getTypeName());
    }
    public FieldKey changeType(TypeKey typeKey){
        return changeType(typeKey.getTypeName());
    }
    public FieldKey changeType(String type){
        if(type.equals(getTypeName())){
            return this;
        }
        return new FieldKey(getDeclaringName(), getName(), type);
    }

    @Override
    public TypeKey getDeclaring() {
        return new TypeKey(getDeclaringName());
    }
    @Override
    public Iterator<Key> mentionedKeys() {
        return CombiningIterator.singleThree(
                FieldKey.this,
                SingleIterator.of(getDeclaring()),
                SingleIterator.of(getNameKey()),
                SingleIterator.of(getType()));
    }
    @Override
    public Key replaceKey(Key search, Key replace) {
        FieldKey result = this;
        if(search.equals(result)){
            return replace;
        }
        if(search.equals(result.getDeclaring())){
            result = result.changeDefining((TypeKey) replace);
        }
        if(search.equals(result.getType())){
            result = result.changeType((TypeKey) replace);
        }
        return result;
    }

    public StringKey getNameKey() {
        return new StringKey(getName());
    }
    public TypeKey getType() {
        return new TypeKey(getTypeName());
    }

    public String getDeclaringName() {
        return declaring;
    }
    public String getName() {
        return name;
    }
    public String getTypeName() {
        return type;
    }

    @Override
    public int compareTo(Object obj) {
        return compareTo(obj, true);
    }
    public int compareTo(Object obj, boolean compareDefining) {
        if(obj == null){
            return -1;
        }
        FieldKey key = (FieldKey) obj;
        int i;
        if(compareDefining){
            i = CompareUtil.compare(getDeclaringName(), key.getDeclaringName());
            if(i != 0) {
                return i;
            }
        }
        i = CompareUtil.compare(getName(), key.getName());
        if(i != 0) {
            return i;
        }
        return CompareUtil.compare(getTypeName(), key.getTypeName());
    }

    @Override
    public int hashCode() {
        int hash = 1;
        String defining = getDeclaringName();
        if(defining != null){
            hash += defining.hashCode();
        }
        return hash * 31 + getName().hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return equals(obj, true, true);
    }
    public boolean equals(Object obj, boolean checkDefining, boolean checkType) {
        if (this == obj) {
            return true;
        }
        if (!(obj instanceof FieldKey)) {
            return false;
        }
        FieldKey fieldKey = (FieldKey) obj;
        if(!KeyUtil.matches(getName(), fieldKey.getName())){
            return false;
        }
        if(checkDefining){
            if(!KeyUtil.matches(getDeclaringName(), fieldKey.getDeclaringName())){
                return false;
            }
        }
        if(checkType){
            return KeyUtil.matches(getTypeName(), fieldKey.getTypeName());
        }
        return true;
    }

    @Override
    public String toString() {
        StringBuilder builder = new StringBuilder();
        builder.append(getDeclaringName());
        builder.append("->");
        builder.append(getName());
        String type = getTypeName();
        if(type != null){
            builder.append(':');
            builder.append(getTypeName());
        }
        return builder.toString();
    }

    public static FieldKey parse(String text) {
        if(text == null){
            return null;
        }
        text = text.trim();
        if(text.length() < 6 || text.charAt(0) != 'L'){
            return null;
        }
        int i = text.indexOf(";->");
        if(i < 0){
            return null;
        }
        String defining = text.substring(0, i + 1);
        text = text.substring(i + 3);
        i = text.indexOf(':');
        if(i < 0){
            return null;
        }
        String name = text.substring(0, i);
        text = text.substring(i + 1);
        String type = null;
        if(!StringsUtil.isEmpty(text)){
            type = text;
        }
        return new FieldKey(defining, name, type);
    }
    public static FieldKey create(FieldId fieldId){
        TypeKey defining = fieldId.getDefining();
        if(defining == null){
            return null;
        }
        String name = fieldId.getName();
        if(name == null) {
            return null;
        }
        TypeKey fieldType = fieldId.getFieldType();
        if(fieldType == null){
            return null;
        }
        return new FieldKey(defining.getTypeName(), name, fieldType.getTypeName());
    }

}