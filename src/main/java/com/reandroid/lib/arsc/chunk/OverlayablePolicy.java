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
 package com.reandroid.lib.arsc.chunk;

 import com.reandroid.lib.arsc.base.Block;
 import com.reandroid.lib.arsc.io.BlockLoad;
 import com.reandroid.lib.arsc.io.BlockReader;
 import com.reandroid.lib.arsc.item.IntegerArray;
 import com.reandroid.lib.arsc.item.IntegerItem;

 import java.io.IOException;
 import java.util.Collection;

 /**
  * Replica of struct "ResTable_overlayable_policy_header" as on AOSP androidfw/ResourceTypes.h
  * We didn't test this class with resource table, if someone found a resource/apk please
  * create issue on https://github.com/REAndroid/ARSCLib
  * */
 public class OverlayablePolicy extends BaseChunk implements BlockLoad {
  private final IntegerItem flags;
  private final IntegerItem entryCount;
  private final IntegerArray tableRefArray;
  public OverlayablePolicy(){
   super(ChunkType.OVERLAYABLE_POLICY, 1);
   this.flags = new IntegerItem();
   this.entryCount = new IntegerItem();
   this.tableRefArray = new IntegerArray();

   addToHeader(this.flags);
   addToHeader(this.entryCount);

   addChild(this.tableRefArray);

   this.entryCount.setBlockLoad(this);
  }
  @Override
  public boolean isNull() {
   return getTableReferenceCount()==0;
  }
  public int getTableReferenceCount(){
   return getTableRefArray().size();
  }

  public Collection<Integer> listTableReferences(){
   return getTableRefArray().toList();
  }
  public IntegerArray getTableRefArray() {
   return tableRefArray;
  }
  public int getFlags() {
   return flags.get();
  }
  public void setFlags(int flags){
   this.flags.set(flags);
  }
  public void setFlags(PolicyFlag[] policyFlags){
   setFlags(PolicyFlag.sum(policyFlags));
  }
  public void addFlag(PolicyFlag policyFlag){
   int i = policyFlag==null? 0 : policyFlag.getFlagValue();
   setFlags(getFlags() | i);
  }
  public PolicyFlag[] getPolicyFlags(){
   return PolicyFlag.valuesOf(getFlags());
  }
  @Override
  protected void onChunkRefreshed() {
   this.entryCount.set(getTableRefArray().size());
  }
  @Override
  public void onBlockLoaded(BlockReader reader, Block sender) throws IOException {
   if(sender==this.entryCount){
    this.tableRefArray.setSize(entryCount.get());
   }
  }
  @Override
  public String toString(){
   return getClass().getSimpleName()+
           ": flags="+ PolicyFlag.toString(getPolicyFlags())
           +"', count="+getTableReferenceCount();
  }

  public enum PolicyFlag {

   PUBLIC(0x00000001),
   SYSTEM_PARTITION(0x00000002),
   VENDOR_PARTITION(0x00000004),
   PRODUCT_PARTITION(0x00000008),
   SIGNATURE(0x00000010),
   ODM_PARTITION(0x00000020),
   OEM_PARTITION(0x00000040),
   ACTOR_SIGNATURE(0x00000080),
   CONFIG_SIGNATURE(0x00000100);

   private final int flag;
   PolicyFlag(int flag) {
    this.flag=flag;
   }
   public int getFlagValue(){
    return this.flag;
   }

   public boolean contains(int flagsValue){
    return (this.flag & flagsValue)==this.flag;
   }

   public static PolicyFlag[] valuesOf(int flagValue){
    if(flagValue==0){
     return null;
    }
    PolicyFlag[] values = values();
    PolicyFlag[] tmp = new PolicyFlag[values.length];
    int count=0;
    for(int i=0;i<values.length;i++){
     PolicyFlag flags = values[i];
     if((flags.getFlagValue() & flagValue)==flags.getFlagValue()){
      tmp[i]=flags;
      count++;
     }
    }
    if(count==0){
     return null;
    }
    if(count==tmp.length){
     return tmp;
    }
    PolicyFlag[] results=new PolicyFlag[count];
    int j=0;
    for(int i=0;i<tmp.length;i++){
     if(tmp[i]!=null){
      results[j]=tmp[i];
      j++;
     }
    }
    return results;
   }

   public static int sum(PolicyFlag[] flagsList){
    if(flagsList==null||flagsList.length==0){
     return 0;
    }
    int results = 0;
    for(PolicyFlag flags:flagsList){
     if(flags!=null){
      results |=flags.getFlagValue();
     }
    }
    return results;
   }
   public static boolean contains(PolicyFlag[] flagsList, PolicyFlag policyFlag){
    if(flagsList==null||flagsList.length==0){
     return policyFlag ==null;
    }
    if(policyFlag ==null){
     return false;
    }
    for(PolicyFlag flags:flagsList){
     if(policyFlag.equals(flags)){
      return true;
     }
    }
    return false;
   }
   public static String toString(PolicyFlag[] flagsList){
    if(flagsList==null || flagsList.length==0){
     return "NONE";
    }
    StringBuilder builder=new StringBuilder();
    boolean appendOnce=false;
    for(PolicyFlag flags:flagsList){
     if(flags==null){
      continue;
     }
     if(appendOnce){
      builder.append('|');
     }
     builder.append(flags.name());
     appendOnce=true;
    }
    if(appendOnce){
     return builder.toString();
    }
    return "NONE";
   }
   public static PolicyFlag[] valuesOf(String flagsString){
    if(flagsString==null){
     return null;
    }
    flagsString=flagsString.trim().toUpperCase();
    String[] namesList=flagsString.split("\\s*\\|\\s*");
    PolicyFlag[] tmp = new PolicyFlag[namesList.length];
    int count=0;
    for(int i=0;i< namesList.length; i++){
     PolicyFlag flags=nameOf(namesList[i]);
     if(flags!=null){
      tmp[i]=flags;
      count++;
     }
    }
    if(count==0){
     return null;
    }
    if(count == tmp.length){
     return tmp;
    }
    PolicyFlag[] results=new PolicyFlag[count];
    int j=0;
    for(int i=0;i<tmp.length;i++){
     if(tmp[i]!=null){
      results[j]=tmp[i];
      j++;
     }
    }
    return results;
   }
   public static PolicyFlag nameOf(String name){
    if(name==null){
     return null;
    }
    name=name.trim().toUpperCase();
    for(PolicyFlag flags:values()){
     if(name.equals(flags.name())){
      return flags;
     }
    }
    return null;
   }
  }
 }
