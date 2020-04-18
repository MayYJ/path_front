package com.may.routeplansystem.entity.vo;

import lombok.Data;

import java.util.List;

@Data
public class DistanceJsonResponse {
    private int status;
    private List<DisAndTime> result;

    @Data
    public class DisAndTime {
        private Dis distance;
        private Duration duration;

        @Data
        public class Dis {
            private String text;
            private int value;
        }

        @Data
        public class Duration {
            private String text;
            private int value;
        }
    }
}
