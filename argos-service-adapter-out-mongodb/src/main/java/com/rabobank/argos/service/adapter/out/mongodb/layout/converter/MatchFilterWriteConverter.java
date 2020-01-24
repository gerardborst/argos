/*
 * Copyright (C) 2019 Rabobank Nederland
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *         http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.rabobank.argos.service.adapter.out.mongodb.layout.converter;

import com.mongodb.BasicDBObject;
import com.mongodb.DBObject;
import com.rabobank.argos.domain.layout.MatchFilter;
import org.springframework.core.convert.converter.Converter;
import org.springframework.data.convert.WritingConverter;

@WritingConverter
public class MatchFilterWriteConverter implements Converter<MatchFilter, DBObject> {

    @Override
    public DBObject convert(MatchFilter matchFilter) {
        DBObject dbo = new BasicDBObject();
        dbo.put("pattern", matchFilter.getPattern());
        dbo.put("destinationStepName", matchFilter.getDestinationStepName());
        dbo.put("destinationType", matchFilter.getDestinationType().name());
        return dbo;
    }
}