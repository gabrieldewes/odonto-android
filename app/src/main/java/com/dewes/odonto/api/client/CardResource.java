package com.dewes.odonto.api.client;

import com.dewes.odonto.domain.Action;
import com.dewes.odonto.domain.Attachment;
import com.dewes.odonto.domain.Card;
import com.dewes.odonto.domain.Status;
import java.util.List;
import retrofit2.Call;
import retrofit2.Response;

/**
 * Created by Dewes on 13/06/2017.
 */

public class CardResource {

    private CardApi cardApi;

    private static CardResource INSTANCE;

    public static CardResource getInstance() {
        if (INSTANCE == null) {
            INSTANCE = new CardResource();
        }
        return INSTANCE;
    }

    private CardResource() {
        this.cardApi = ServiceGenerator.createService(CardApi.class);
    }

    public Call create(String whatafield, final Callback<Status<Card>> callback) {
        Card card = new Card();
        card.setWhatafield(whatafield);
        Call<Status<Card>> call = this.cardApi.save(card);
        call.enqueue(new retrofit2.Callback<Status<Card>>() {
            @Override
            public void onResponse(Call<Status<Card>> call, Response<Status<Card>> response) {
                callback.onResult(response.body());
            }

            @Override
            public void onFailure(Call<Status<Card>> call, Throwable t) {
                t.printStackTrace();
                callback.onError();
            }
        });
        return call;
    }

    public Call archive(boolean archive, Long cardId, final Callback<Status<Card>> callback) {
        Call<Status<Card>> call = this.cardApi.archive(archive, cardId);
        call.enqueue(new retrofit2.Callback<Status<Card>>() {
            @Override
            public void onResponse(Call<Status<Card>> call, Response<Status<Card>> response) {
                callback.onResult(response.body());
            }

            @Override
            public void onFailure(Call<Status<Card>> call, Throwable t) {
                t.printStackTrace();
                callback.onError();
            }
        });
        return call;
    }

    public Call findAll(boolean archive, int page, final Callback<List<Card>> callback) {
        if (archive) {
            return this.findAllArchived(page, callback);
        }
        else {
            return this.findAll(page, callback);
        }
    }

    public Call findAll(int page, final Callback<List<Card>> callback) {
        Call<List<Card>> call = this.cardApi.findAll(page);
        call.enqueue(new retrofit2.Callback<List<Card>>() {
            @Override
            public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                callback.onResult(response.body());
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                t.printStackTrace();
                if (!call.isCanceled())
                    callback.onError();
            }
        });
        return call;
    }

    public Call findAllArchived(int page, final Callback<List<Card>> callback) {
        Call<List<Card>> call = this.cardApi.findAllArchived(page);
        call.enqueue(new retrofit2.Callback<List<Card>>() {
            @Override
            public void onResponse(Call<List<Card>> call, Response<List<Card>> response) {
                callback.onResult(response.body());
            }

            @Override
            public void onFailure(Call<List<Card>> call, Throwable t) {
                t.printStackTrace();
                if (!call.isCanceled())
                    callback.onError();
            }
        });
        return call;
    }

    public Call findById(Long cardId, final Callback<Card> callback) {
        Call<Card> call = this.cardApi.findById(cardId);
        call.enqueue(new retrofit2.Callback<Card>() {
            @Override
            public void onResponse(Call<Card> call, Response<Card> response) {
                callback.onResult(response.body());
            }

            @Override
            public void onFailure(Call<Card> call, Throwable t) {
                callback.onError();
            }
        });
        return call;
    }

    public Call getAttachments(Long cardId, final Callback<List<Attachment>> callback) {
        Call<List<Attachment>> call = this.cardApi.getAttachments(cardId);
        call.enqueue(new retrofit2.Callback<List<Attachment>>() {
            @Override
            public void onResponse(Call<List<Attachment>> call, Response<List<Attachment>> response) {
                callback.onResult(response.body());
            }

            @Override
            public void onFailure(Call<List<Attachment>> call, Throwable t) {
                callback.onError();
            }
        });
        return call;
    }

    public Call findAttachmentById(Long cardId, Long attachmentId, final Callback<Attachment> callback) {
        Call<Attachment> call = this.cardApi.findAttachmentById(cardId, attachmentId);
        call.enqueue(new retrofit2.Callback<Attachment>() {
            @Override
            public void onResponse(Call<Attachment> call, Response<Attachment> response) {
                callback.onResult(response.body());
            }

            @Override
            public void onFailure(Call<Attachment> call, Throwable t) {
                callback.onError();
            }
        });
        return call;
    }

    public Call getActions(Long cardId, int page, final Callback<List<Action>> callback) {
        Call<List<Action>> call = this.cardApi.getActions(cardId, page);
        call.enqueue(new retrofit2.Callback<List<Action>>() {
            @Override
            public void onResponse(Call<List<Action>> call, Response<List<Action>> response) {
                callback.onResult(response.body());
            }

            @Override
            public void onFailure(Call<List<Action>> call, Throwable t) {
                callback.onError();
            }
        });
        return call;
    }

    public Call findActionById(Long cardId, Long actionId, final Callback<Action> callback) {
        Call<Action> call = this.cardApi.findActionById(cardId, actionId);
        return call;
    }

    public Call getActionAttachments(Long cardId, Long actionId, final Callback<List<Attachment>> callback) {
        Call<List<Attachment>> call = this.cardApi.getActionAttachments(cardId, actionId);
        return call;
    }

    public Call findActionAttachmentById(Long cardId, Long actionId, Long attachmentId, final Callback<Attachment> callback) {
        Call<Attachment> call = this.cardApi.findActionAttachmentById(cardId, actionId, attachmentId);
        return call;
    }

}
