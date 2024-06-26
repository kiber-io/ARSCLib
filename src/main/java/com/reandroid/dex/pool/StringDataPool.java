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
package com.reandroid.dex.pool;

import com.reandroid.dex.data.StringData;
import com.reandroid.dex.id.StringId;
import com.reandroid.dex.key.Key;
import com.reandroid.dex.sections.Section;
import com.reandroid.dex.sections.SectionList;
import com.reandroid.dex.sections.SectionType;
import com.reandroid.dex.sections.StringDataSection;
import com.reandroid.utils.collection.ComputeIterator;
import com.reandroid.utils.collection.EmptyIterator;

import java.util.Iterator;

public class StringDataPool extends DexSectionPool<StringData> {

    public StringDataPool(StringDataSection section) {
        super(section);
    }

    // Ignore loading, StringId pool is already created.
    @Override
    public void load() {
    }

    @Override
    public int clearDuplicates() {
        return 0;
    }

    @Override
    public int size() {
        DexSectionPool<StringId> pool = getLoadedIdPool();
        if(pool != null){
            return pool.size();
        }
        return 0;
    }

    @Override
    public StringData get(Key key) {
        return getStringData(key);
    }
    @Override
    public Iterator<StringData> getAll(Key key){
        DexSectionPool<StringId> pool = getLoadedIdPool();
        if(pool == null){
            return EmptyIterator.of();
        }
        return ComputeIterator.of(pool.getAll(key),
                StringId::getStringData);
    }

    @Override
    public boolean updateKey(Key old, Key key, StringData value) {
        DexSectionPool<StringId> pool = getLoadedIdPool();
        if(pool != null) {
            return pool.updateKey(old, key, pool.get(old));
        }
        return false;
    }

    @Override
    public boolean contains(Key key) {
        DexSectionPool<StringId> pool = getIdPool();
        if(pool != null){
            return pool.contains(key);
        }
        return false;
    }

    @Override
    StringDataSection getSection() {
        return (StringDataSection) super.getSection();
    }
    @Override
    boolean isKeyItemsCreate() {
        return false;
    }

    private StringData getStringData(Key key) {
        DexSectionPool<StringId> pool = getIdPool();
        if(pool == null){
            return null;
        }
        StringId stringId = pool.get(key);
        if(stringId != null){
            return stringId.getStringData();
        }
        return null;
    }
    private DexSectionPool<StringId> getIdPool(){
        Section<StringId> section = getStringIdSection();
        if(section != null){
            return section.getPool();
        }
        return null;
    }
    private DexSectionPool<StringId> getLoadedIdPool(){
        Section<StringId> section = getStringIdSection();
        if(section != null){
            return section.getLoadedPool();
        }
        return null;
    }
    private Section<StringId> getStringIdSection(){
        StringDataSection section = getSection();
        SectionList sectionList = section.getSectionList();
        if(sectionList != null){
            return sectionList.getSection(SectionType.STRING_ID);
        }
        return null;
    }
}
