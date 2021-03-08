package com.app.billmanager.configuration;

import lombok.Data;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.stereotype.Component;

@Component
@ConfigurationProperties(prefix = "bill.data")
@Data
public class BillProps {

    private int pageSize = 8;

}
