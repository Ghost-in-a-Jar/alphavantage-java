package com.crazzyghost.alphavantage.indicator.response;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.crazzyghost.alphavantage.parser.DefaultParser;
import com.crazzyghost.alphavantage.parser.Parser;

public class PeriodicSeriesResponse {

    private MetaData metaData;
    private List<SimpleIndicatorUnit> indicatorUnits;
    private String errorMessage;

    private PeriodicSeriesResponse(List<SimpleIndicatorUnit> indicatorUnits, MetaData metaData){
        this.metaData = metaData;
        this.indicatorUnits = indicatorUnits;
        this.errorMessage = null;
    }

    private PeriodicSeriesResponse(String errorMessage){
        this.metaData = new MetaData();
        this.indicatorUnits = new ArrayList<>();
        this.errorMessage = errorMessage;
    }

    public String getErrorMessage() {
        return errorMessage;
    }

    public List<SimpleIndicatorUnit> getIndicatorUnits() {
        return indicatorUnits;
    }
    
    public MetaData getMetaData() {
        return metaData;
    }
    
    public static PeriodicSeriesResponse of(Map<String, Object> stringObjectMap, String indicatorKey){
        Parser<PeriodicSeriesResponse> parser = new PeriodicSeriesParser(indicatorKey);
        return parser.parse(stringObjectMap);
    }

    public static class PeriodicSeriesParser extends DefaultParser<PeriodicSeriesResponse> {

        private String indicatorKey;

        public PeriodicSeriesParser(String indicatorKey){
            this.indicatorKey = indicatorKey;
        }

        @Override
        public PeriodicSeriesResponse parse(Map<String, String> metaDataMap, Map<String, Map<String, String>> indicatorData) {
            
            MetaData metaData = new MetaData(
                String.valueOf(metaDataMap.get("1: Symbol")),
                String.valueOf(metaDataMap.get("2: Indicator")),
                String.valueOf(metaDataMap.get("3: Last Refreshed")),
                String.valueOf(metaDataMap.get("4: Interval")),
                String.valueOf(metaDataMap.get("7: Time Zone")),
                String.valueOf(metaDataMap.get("6: Series Type")),
                (int)Double.parseDouble(String.valueOf(metaDataMap.get("5: Time Period")))
            );

            List<SimpleIndicatorUnit> indicatorUnits =  new ArrayList<>();

            for (Map.Entry<String,Map<String,String>> e: indicatorData.entrySet()) {
                Map<String, String> m = e.getValue();     
                SimpleIndicatorUnit indicatorUnit = new SimpleIndicatorUnit(
                    e.getKey(),
                    Double.parseDouble(m.get(indicatorKey)),
                    indicatorKey
                );
                indicatorUnits.add(indicatorUnit);
            }
            return new PeriodicSeriesResponse(indicatorUnits, metaData);
        }

        @Override
        public PeriodicSeriesResponse onParseError(String error) {
            return new PeriodicSeriesResponse(error);
        }
    }

    @Override
    public String toString() {
        return metaData.indicator.replaceAll("\\s+","") +"Response{" +
            "metaData=" + metaData +
            ",indicatorUnits=" + indicatorUnits.size() +
            ", errorMessage='" + errorMessage + '\'' +
        '}';
    }

    public static class MetaData {

        private String symbol;
        private String indicator;
        private String lastRefreshed;
        private String interval;
        private String timeZone;
        private String seriesType;
        private int timePeriod;

        public MetaData(){
            this("", "", "", "", "", "", 0);
        }

        public MetaData(
            String symbol, 
            String indicator, 
            String lastRefreshed, 
            String interval, 
            String timeZone,
            String seriesType, 
            int timePeriod
        ) {
            this.symbol = symbol;
            this.indicator = indicator;
            this.lastRefreshed = lastRefreshed;
            this.interval = interval;
            this.timeZone = timeZone;
            this.seriesType = seriesType;
            this.timePeriod = timePeriod;
        }

        public String getSymbol() {
            return symbol;
        }

        public String getIndicator() {
            return indicator;
        }

        public String getLastRefreshed() {
            return lastRefreshed;
        }

        public String getInterval() {
            return interval;
        }

        public String getTimeZone() {
            return timeZone;
        }

        public String getSeriesType() {
            return seriesType;
        }

        public int getTimePeriod() {
            return timePeriod;
        }

        @Override
        public String toString() {
            return "MetaData {indicator=" + indicator +     
                ", interval=" + interval + 
                ", lastRefreshed=" + lastRefreshed + 
                ", seriesType=" + seriesType + 
                ", symbol=" + symbol + 
                ", timePeriod=" + timePeriod + 
                ", timeZone=" + timeZone +
            "}";
        }   
    }
}



