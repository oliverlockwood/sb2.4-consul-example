package com.oliverlockwood.example;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;


@Component
public class PropertiesConfiguration {

    @Value("${alpha:alpha-default}")
    public String alpha;

    @Value("${bravo:bravo-default}")
    public String bravo;

    @Value("${charlie:charlie-default}")
    public String charlie;

    @Value("${delta:delta-default}")
    public String delta;

    @Value("${echo:echo-default}")
    public String echo;

    @Value("${foxtrot:foxtrot-default}")
    public String foxtrot;

    @Value("${gamma:gamma-default}")
    public String gamma;

    @Value("${hotel:hotel-default}")
    public String hotel;

}
