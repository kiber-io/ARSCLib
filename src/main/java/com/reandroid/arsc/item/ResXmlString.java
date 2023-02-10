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
package com.reandroid.arsc.item;

import java.util.List;

public class ResXmlString extends StringItem {
    public ResXmlString(boolean utf8) {
        super(utf8);
    }
    public ResXmlString(boolean utf8, String value) {
        this(utf8);
        set(value);
    }
    @Override
    public String toString(){
        List<ReferenceItem> refList = getReferencedList();
        return "USED BY="+refList.size()+"{"+super.toString()+"}";
    }
}