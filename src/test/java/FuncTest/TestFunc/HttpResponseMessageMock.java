package FuncTest.TestFunc;

import com.microsoft.azure.functions.*;

import java.util.Map;

/**
 * The mock for HttpResponseMessage, can be used in unit tests to verify if the
 * returned response by HTTP trigger function is correct or not.
 */
public class HttpResponseMessageMock implements HttpResponseMessage {
    private int httpStatusCode;
    private HttpStatusType httpStatus;
    private Object body;
    private Map<String, String> headers;

    public HttpResponseMessageMock(HttpStatusType status, Map<String, String> headers, Object body) {
        this.httpStatus = status;
        this.httpStatusCode = status.value();
        this.headers = headers;
        this.body = body;
    }

    @Override
    public HttpStatusType getStatus() {
        return this.httpStatus;
    }

    @Override
    public int getStatusCode() {
        return this.httpStatusCode;
    }

    @Override
    public String getHeader(String key) {
        return this.headers.get(key);
    }

    @Override
    public Object getBody() {
        return this.body;
    }

    public static class HttpResponseMessageBuilderMock implements HttpResponseMessage.Builder {
        private Object body;
        @SuppressWarnings("unused")
		private int httpStatusCode;
        private Map<String, String> headers;
        private HttpStatusType httpStatus;

        public Builder status(HttpStatus s) {
            this.httpStatusCode = s.value();
            this.httpStatus = s;
            return this;
        }

        @Override
        public Builder status(HttpStatusType t) {
            this.httpStatusCode = t.value();
            this.httpStatus = t;
            return this;
        }

        @Override
        public HttpResponseMessage.Builder header(String key, String value) {
            this.headers.put(key, value);
            return this;
        }

        @Override
        public HttpResponseMessage.Builder body(Object body) {
            this.body = body;
            return this;
        }

        @Override
        public HttpResponseMessage build() {
            return new HttpResponseMessageMock(this.httpStatus, this.headers, this.body);
        }
    }
}
