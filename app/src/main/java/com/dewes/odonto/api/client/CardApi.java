package com.dewes.odonto.api.client;

import com.dewes.odonto.domain.Action;
import com.dewes.odonto.domain.Attachment;
import com.dewes.odonto.domain.Card;
import com.dewes.odonto.domain.Status;

import java.util.List;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.GET;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.DELETE;
import retrofit2.http.Path;
import retrofit2.http.Query;

/**
 * Created by Dewes on 13/06/2017.
 */

public interface CardApi {

    @GET("cards")
    Call<List<Card>> findAll(@Query("page") int page);

    @GET("cards/archive")
    Call<List<Card>> findAllArchived(@Query("page") int page);

    @GET("cards/{cardId}")
    Call<Card> findById(@Path("cardId") Long cardId);

    @PUT("cards/{cardId}/archive")
    Call<Status<Card>> archive(@Body Boolean archive, @Path("cardId") Long cardId);

    @POST("cards")
    Call<Status<Card>> save(@Body Card c);

    @PUT("cards/{cardId}")
    Call<Card> update(@Body Card c, @Path("cardId") Long cardId);

    @DELETE("cards/{cardId}")
    Call<Boolean> delete(@Path("cardId") Long cardId);

    @GET("cards/{cardId}/attachments")
    Call<List<Attachment>> getAttachments(@Path("cardId") Long cardId);

    @GET("cards/{cardId}/attachments/{attachmentId}")
    Call<Attachment> findAttachmentById(@Path("cardId") Long cardId, @Path("attachmentId") Long attachmentId);

    @GET("cards/{cardId}/actions")
    Call<List<Action>> getActions(@Path("cardId") Long cardId, @Query("page") int page);

    @GET("cards/{cardId}/actions/{actionId}")
    Call<Action> findActionById(@Path("cardId") Long cardId, @Path("actionId") Long actionId);

    @GET("cards/{cardId}/actions/{actionId}/attachments")
    Call<List<Attachment>> getActionAttachments(@Path("cardId") Long cardId, @Path("actionId") Long actionId);

    @GET("cards/{cardId}/actions/{actionId}/attachments/{attachmentId}")
    Call<Attachment> findActionAttachmentById(@Path("cardId") Long cardId, @Path("actionId") Long actionId, @Path("attachmentId") Long attachmentId);
}
