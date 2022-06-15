package com.example.followread;

public class highLights_Item {
    private String HLTitle, HLContent, HLPage, HLChapter, HLId;

    public highLights_Item(String HLPage, String HLChapter, String HLId, String HLTitle, String HLContent) {
        this.HLPage = HLPage;
        this.HLChapter = HLChapter;
        this.HLId = HLId;
        this.HLTitle = HLTitle;
        this.HLContent = HLContent;
    }

    public String getHLPage(){
        return HLPage;
    }
    public String getHLChapter(){
        return HLChapter;
    }
    public String getHLId() {
        return HLId;
    }
    public String getHLContent() {
        return HLContent;
    }
    public String getHLTitle() {
        return HLTitle;
    }
}
