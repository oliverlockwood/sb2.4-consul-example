/*
 * Copyright © 2020 Dalet - All Rights Reserved
 *
 * This file is part of Ooyala Flex.
 *
 * Unauthorized copying and/or distribution of this file or any other part of Ooyala Flex, via any medium,
 * is strictly prohibited.  Proprietary and confidential.
 */
package com.oliverlockwood.example.controller;

import com.oliverlockwood.example.contract.ExampleContract;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.RestController;


@Slf4j
@RestController
public class ExampleController implements ExampleContract {

    @Value("${alpha:alpha-default}")
    private String alpha;

    @Value("${bravo:bravo-default}")
    private String bravo;

    @Value("${charlie:charlie-default}")
    private String charlie;

    @Value("${delta:delta-default}")
    private String delta;

    @Value("${echo:echo-default}")
    private String echo;

    @Value("${foxtrot:foxtrot-default}")
    private String foxtrot;

    @Value("${gamma:gamma-default}")
    private String gamma;

    @Value("${hotel:hotel-default}")
    private String hotel;

    @Autowired
    private String configInfoString;


    @Override
    public String example() {

        String value = String.format("Values are:\n  %s\n  %s\n  %s\n  %s\n  %s\n  %s\n  %s\n  %s",
                alpha, bravo, charlie, delta, echo, foxtrot, gamma, hotel);

        log.info(value);
        return value + "\n";
    }


    @Override
    public String example2() {

        log.info(configInfoString);
        return configInfoString + "\n";
    }

}
