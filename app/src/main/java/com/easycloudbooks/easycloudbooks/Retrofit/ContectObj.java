package com.easycloudbooks.easycloudbooks.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ContectObj {
    @SerializedName("Pos")
    @Expose
    private Object pos;
    @SerializedName("RN")
    @Expose
    private Object rN;
    @SerializedName("IO")
    @Expose
    private Boolean iO;
    @SerializedName("IB")
    @Expose
    private Boolean iB;
    @SerializedName("ACId")
    @Expose
    private Integer aCId;
    @SerializedName("GId")
    @Expose
    private Integer gId;
    @SerializedName("ACIds")
    @Expose
    private Object aCIds;
    @SerializedName("CACIds")
    @Expose
    private Object cACIds;
    @SerializedName("ASId")
    @Expose
    private Integer aSId;
    @SerializedName("Id")
    @Expose
    private Integer id;
    @SerializedName("FN")
    @Expose
    private String fN;
    @SerializedName("MN")
    @Expose
    private String mN;
    @SerializedName("LN")
    @Expose
    private String lN;
    @SerializedName("S")
    @Expose
    private String s;
    @SerializedName("T")
    @Expose
    private String t;
    @SerializedName("DD")
    @Expose
    private Object dD;
    @SerializedName("CD")
    @Expose
    private String cD;
    @SerializedName("Note")
    @Expose
    private String note;
    @SerializedName("I")
    @Expose
    private String i;
    @SerializedName("IC")
    @Expose
    private Boolean iC;
    @SerializedName("PE")
    @Expose
    private Object pE;
    @SerializedName("PPh")
    @Expose
    private Object pPh;
    @SerializedName("PPhC")
    @Expose
    private Object pPhC;
    @SerializedName("UN")
    @Expose
    private Object uN;
    @SerializedName("Es")
    @Expose
    private List<ContectE> es = null;
    @SerializedName("Fs")
    @Expose
    private List<Object> fs = null;
    @SerializedName("Ds")
    @Expose
    private List<ContectD> ds = null;
    @SerializedName("Is")
    @Expose
    private Object is;
    @SerializedName("PHs")
    @Expose
    private List<ContectPH> pHs = null;
    @SerializedName("Adds")
    @Expose
    private List<ContectAdd> adds = null;
    @SerializedName("CRUs")
    @Expose
    private Object cRUs;
    @SerializedName("Bs")
    @Expose
    private Object bs;
    @SerializedName("B")
    @Expose
    private Object b;
    @SerializedName("Ss")
    @Expose
    private Object ss;
    @SerializedName("IF")
    @Expose
    private Boolean iF;
    @SerializedName("Token")
    @Expose
    private Object token;
    @SerializedName("LLD")
    @Expose
    private Object lLD;
    @SerializedName("G")
    @Expose
    private String g;
    @SerializedName("UId")
    @Expose
    private String uId;
    @SerializedName("DACId")
    @Expose
    private Integer dACId;
    @SerializedName("RoleJ")
    @Expose
    private Object roleJ;
    @SerializedName("GlobalUser")
    @Expose
    private Object globalUser;
    @SerializedName("CL")
    @Expose
    private Object cL;
    @SerializedName("LUId")
    @Expose
    private Object lUId;
    @SerializedName("CUId")
    @Expose
    private Object cUId;

    public Object getPos() {
        return pos;
    }

    public void setPos(Object pos) {
        this.pos = pos;
    }

    public Object getRN() {
        return rN;
    }

    public void setRN(Object rN) {
        this.rN = rN;
    }

    public Boolean getIO() {
        return iO;
    }

    public void setIO(Boolean iO) {
        this.iO = iO;
    }

    public Boolean getIB() {
        return iB;
    }

    public void setIB(Boolean iB) {
        this.iB = iB;
    }

    public Integer getACId() {
        return aCId;
    }

    public void setACId(Integer aCId) {
        this.aCId = aCId;
    }

    public Integer getGId() {
        return gId;
    }

    public void setGId(Integer gId) {
        this.gId = gId;
    }

    public Object getACIds() {
        return aCIds;
    }

    public void setACIds(Object aCIds) {
        this.aCIds = aCIds;
    }

    public Object getCACIds() {
        return cACIds;
    }

    public void setCACIds(Object cACIds) {
        this.cACIds = cACIds;
    }

    public Integer getASId() {
        return aSId;
    }

    public void setASId(Integer aSId) {
        this.aSId = aSId;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getFN() {
        return fN;
    }

    public void setFN(String fN) {
        this.fN = fN;
    }

    public String getMN() {
        return mN;
    }

    public void setMN(String mN) {
        this.mN = mN;
    }

    public String getLN() {
        return lN;
    }

    public void setLN(String lN) {
        this.lN = lN;
    }

    public String getS() {
        return s;
    }

    public void setS(String s) {
        this.s = s;
    }

    public String getT() {
        return t;
    }

    public void setT(String t) {
        this.t = t;
    }

    public Object getDD() {
        return dD;
    }

    public void setDD(Object dD) {
        this.dD = dD;
    }

    public String getCD() {
        return cD;
    }

    public void setCD(String cD) {
        this.cD = cD;
    }

    public String getNote() {
        return note;
    }

    public void setNote(String note) {
        this.note = note;
    }

    public String getI() {
        return i;
    }

    public void setI(String i) {
        this.i = i;
    }

    public Boolean getIC() {
        return iC;
    }

    public void setIC(Boolean iC) {
        this.iC = iC;
    }

    public Object getPE() {
        return pE;
    }

    public void setPE(Object pE) {
        this.pE = pE;
    }

    public Object getPPh() {
        return pPh;
    }

    public void setPPh(Object pPh) {
        this.pPh = pPh;
    }

    public Object getPPhC() {
        return pPhC;
    }

    public void setPPhC(Object pPhC) {
        this.pPhC = pPhC;
    }

    public Object getUN() {
        return uN;
    }

    public void setUN(Object uN) {
        this.uN = uN;
    }

    public List<ContectE> getEs() {
        return es;
    }

    public void setEs(List<ContectE> es) {
        this.es = es;
    }

    public List<Object> getFs() {
        return fs;
    }

    public void setFs(List<Object> fs) {
        this.fs = fs;
    }

    public List<ContectD> getDs() {
        return ds;
    }

    public void setDs(List<ContectD> ds) {
        this.ds = ds;
    }

    public Object getIs() {
        return is;
    }

    public void setIs(Object is) {
        this.is = is;
    }

    public List<ContectPH> getPHs() {
        return pHs;
    }

    public void setPHs(List<ContectPH> pHs) {
        this.pHs = pHs;
    }

    public List<ContectAdd> getAdds() {
        return adds;
    }

    public void setAdds(List<ContectAdd> adds) {
        this.adds = adds;
    }

    public Object getCRUs() {
        return cRUs;
    }

    public void setCRUs(Object cRUs) {
        this.cRUs = cRUs;
    }

    public Object getBs() {
        return bs;
    }

    public void setBs(Object bs) {
        this.bs = bs;
    }

    public Object getB() {
        return b;
    }

    public void setB(Object b) {
        this.b = b;
    }

    public Object getSs() {
        return ss;
    }

    public void setSs(Object ss) {
        this.ss = ss;
    }

    public Boolean getIF() {
        return iF;
    }

    public void setIF(Boolean iF) {
        this.iF = iF;
    }

    public Object getToken() {
        return token;
    }

    public void setToken(Object token) {
        this.token = token;
    }

    public Object getLLD() {
        return lLD;
    }

    public void setLLD(Object lLD) {
        this.lLD = lLD;
    }

    public String getG() {
        return g;
    }

    public void setG(String g) {
        this.g = g;
    }

    public String getUId() {
        return uId;
    }

    public void setUId(String uId) {
        this.uId = uId;
    }

    public Integer getDACId() {
        return dACId;
    }

    public void setDACId(Integer dACId) {
        this.dACId = dACId;
    }

    public Object getRoleJ() {
        return roleJ;
    }

    public void setRoleJ(Object roleJ) {
        this.roleJ = roleJ;
    }

    public Object getGlobalUser() {
        return globalUser;
    }

    public void setGlobalUser(Object globalUser) {
        this.globalUser = globalUser;
    }

    public Object getCL() {
        return cL;
    }

    public void setCL(Object cL) {
        this.cL = cL;
    }

    public Object getLUId() {
        return lUId;
    }

    public void setLUId(Object lUId) {
        this.lUId = lUId;
    }

    public Object getCUId() {
        return cUId;
    }

    public void setCUId(Object cUId) {
        this.cUId = cUId;
    }
}
