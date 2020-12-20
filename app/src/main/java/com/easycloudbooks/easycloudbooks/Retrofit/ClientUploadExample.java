package com.easycloudbooks.easycloudbooks.Retrofit;

import com.google.gson.annotations.Expose;
import com.google.gson.annotations.SerializedName;

import java.util.List;

public class ClientUploadExample {
    @SerializedName("added")
    @Expose
    private List<ClientDocumenteUpload> added = null;
    @SerializedName("_chunkmerged")
    @Expose
    private Object chunkmerged;
    @SerializedName("_name")
    @Expose
    private Object name;
    @SerializedName("unotherized")
    @Expose
    private List<Object> unotherized = null;
    @SerializedName("hashes")
    @Expose
    private ClientHash hashes;

    public List<ClientDocumenteUpload> getAdded() {
        return added;
    }

    public void setAdded(List<ClientDocumenteUpload> added) {
        this.added = added;
    }

    public Object getChunkmerged() {
        return chunkmerged;
    }

    public void setChunkmerged(Object chunkmerged) {
        this.chunkmerged = chunkmerged;
    }

    public Object getName() {
        return name;
    }

    public void setName(Object name) {
        this.name = name;
    }

    public List<Object> getUnotherized() {
        return unotherized;
    }

    public void setUnotherized(List<Object> unotherized) {
        this.unotherized = unotherized;
    }

    public ClientHash getHashes() {
        return hashes;
    }

    public void setHashes(ClientHash hashes) {
        this.hashes = hashes;
    }

}
