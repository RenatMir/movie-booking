package com.renatmirzoev.moviebookingservice.rest.model.auditorium;

import lombok.Data;
import lombok.experimental.Accessors;

import java.util.SortedSet;

@Data
@Accessors(chain = true)
public class GetAuditoriumResponse {
    private long id;
    private String name;
    private long theaterId;
    private SortedSet<Row> rows;

    @Data
    @Accessors(chain = true)
    public static class Row implements Comparable<Row> {
        private long id;
        private long label;
        private SortedSet<Seat> seats;

        @Override
        public int compareTo(Row o) {
            return Long.compare(label, o.label);
        }

        @Data
        @Accessors(chain = true)
        public static class Seat implements Comparable<Seat> {
            private long id;
            private long label;

            @Override
            public int compareTo(Seat o) {
                return Long.compare(label, o.label);
            }
        }
    }
}
