/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2015, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */

package org.jboss.hal.testsuite.test.runtime.batch;

import java.util.concurrent.TimeUnit;
import java.util.logging.Logger;

import javax.batch.api.BatchProperty;
import javax.batch.api.chunk.ItemProcessor;
import javax.inject.Inject;
import javax.inject.Named;

/**
 * Batch {@link ItemProcessor} to be used in testing batch jobs in {@link BatchManagementTestCase}
 * @author pjelinek
 */
@Named("testProcessor")
public class Processor implements ItemProcessor {

    private Logger log = Logger.getLogger(Processor.class.getName());

    @Inject
    @BatchProperty
    private Long itemProcessingSleep;

    @Override
    public String processItem(Object item) throws Exception {
        if (itemProcessingSleep != null && itemProcessingSleep > 0L) {
            log.info("Processing slowly " + item + " with " + itemProcessingSleep + "ms sleep.");
            TimeUnit.MILLISECONDS.sleep(itemProcessingSleep);
        }
        return ((String) item).toUpperCase();
    }

}
