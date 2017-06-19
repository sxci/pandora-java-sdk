package com.qiniu.pandora.logdb;

import com.qiniu.pandora.common.PandoraClient;
import com.qiniu.pandora.common.QiniuException;
import com.qiniu.pandora.http.Client;
import com.qiniu.pandora.http.Response;
import com.qiniu.pandora.util.Json;
import com.qiniu.pandora.util.StringMap;
import com.qiniu.pandora.util.StringUtils;

/**
 *  可以使用该接口对logdb的数据进行Dump。需要配合{@link #SearchService}服务，可重用。
 */
public class ScrollService  implements Reusable {
    private LogDBClient logDBClient;
    private String path = Constant.POST_SCROLL;
    private ScrollRequest scrollRequest = new ScrollRequest();

    public ScrollService(LogDBClient logDBClient) {
        this.logDBClient = logDBClient;
    }
    public ScrollService setScroll(String scroll) {
        this.scrollRequest.setScroll(scroll);
        return this;
    }

    /**
     *
     * @param scroll_id 使用上次返回的scroll_id来持续查询
     * @return
     */
    public ScrollService setScroll_id(String scroll_id) {
        this.scrollRequest.setScroll_id(scroll_id);
        return this;
    }

    public SearchService.SearchRet action()throws QiniuException{
        PandoraClient pandoraClient = this.logDBClient.getPandoraClient();
        Response response = pandoraClient.post(this.logDBClient.getHost() + this.path, this.scrollRequest.ToJsonBytes(), new StringMap(), Client.JsonMime);
        SearchService.SearchRet searchRet =  Json.decode(response.bodyString(), SearchService.SearchRet.class);
        searchRet.setResponse(response);
        return searchRet;
    }

    @Override
    public void reset() {
        this.scrollRequest = new ScrollRequest();
    }

    static class ScrollRequest {
        private String scroll;
        private String scroll_id;

        public ScrollRequest() {
        }

        public String getScroll() {
            return scroll;
        }

        public void setScroll(String scroll) {
            this.scroll = scroll;
        }

        public String getScroll_id() {
            return scroll_id;
        }

        public void setScroll_id(String scroll_id) {
            this.scroll_id = scroll_id;
        }

        public byte[] ToJsonBytes(){
            return StringUtils.utf8Bytes(Json.encode(this));
        }
    }
}
