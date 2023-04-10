package com.company.web.smart_garage.enums;

public enum VehicleParam {
    LICENSE_PLATE("license-plate"),
    VIN("vin"),
    OWNER("owner"),
    PROD_YEAR_FROM("prod-year-from"),
    PROD_YEAR_TO("prod-year-to"),
    MODEL("model"),
    BRAND("brand"),
    SORT_BY("sort-by"),
    SORT_ORD("sort-order"),
    PAGE("page");

    private final String paramName;

    VehicleParam(String paramName) {
        this.paramName = paramName;
    }

    public String getParamName() {
        return paramName;
    }
}
