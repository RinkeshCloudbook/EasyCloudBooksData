package com.easycloudbooks.easycloudbooks.Retrofit;

import okhttp3.MultipartBody;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.http.Header;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Url;

public interface FileUploadService {
    @Multipart
    @POST
    Call<UserFileUpload> uploadImage(@Url String url,@Header ("Authorization")String token,@Part MultipartBody.Part fileToUpload, @Part("mode") RequestBody mode, @Part("filepath") RequestBody filepath,
                                     @Part("crid") RequestBody crid);
    @Multipart
    @POST
    Call<ContectExample> contectuploadImage(@Url String url,@Header ("Authorization")String token,@Part MultipartBody.Part fileToUpload, @Part("mode") RequestBody mode, @Part("filepath") RequestBody filepath,
                                     @Part("coid") RequestBody coid);

    @Multipart
    @POST
    Call<ClientUploadExample> uploadImage(@Url String url,
                                          @Header("Authorization") String token,
                                          @Part MultipartBody.Part[] fileToUpload,
                                          @Part("target") RequestBody target,
                                          @Part("CRId") RequestBody crId,
                                          @Part("upload") RequestBody upload,
                                          @Part("cmd") RequestBody cmd);
}
