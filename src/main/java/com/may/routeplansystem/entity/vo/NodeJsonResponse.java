package com.may.routeplansystem.entity.vo;

import lombok.Data;

@Data
public class NodeJsonResponse {

    private int status;
    private Result result;

    @Data
    public class Result {
        private Location location;
        private String level;

        @Data
        public class Location {
            private double lng;
            private double lat;
        }
    }

}
