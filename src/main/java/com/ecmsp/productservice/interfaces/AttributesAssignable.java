package com.ecmsp.productservice.interfaces;

import java.math.BigDecimal;
import java.time.LocalDate;

public interface AttributesAssignable {
    void setValueText(String value);
    void setValueDecimal(BigDecimal value);
    void setValueBoolean(Boolean value);
    void setValueDate(LocalDate value);
}
