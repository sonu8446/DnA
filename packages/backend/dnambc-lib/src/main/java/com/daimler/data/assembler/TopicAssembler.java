/* LICENSE START
 * 
 * MIT License
 * 
 * Copyright (c) 2019 Daimler TSS GmbH
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 * 
 * LICENSE END 
 */

package com.daimler.data.assembler;

import com.daimler.data.db.entities.TopicNsql;
import com.daimler.data.db.jsonb.Topic;
import com.daimler.data.dto.topic.TopicVO;
import org.springframework.stereotype.Component;

import java.util.Objects;

@Component
public class TopicAssembler
        implements GenericAssembler<TopicVO, TopicNsql> {

    @Override
    public TopicVO toVo(TopicNsql entity) {
        TopicVO topicVO = null;
        if (Objects.nonNull(entity)) {
            topicVO = new TopicVO();
            topicVO.setId(entity.getId());
            topicVO.setName(entity.getData().getName());
        }
        return topicVO;
    }

    @Override
    public TopicNsql toEntity(TopicVO vo) {
        TopicNsql topicNsql = null;
        if (Objects.nonNull(vo)) {
            topicNsql = new TopicNsql();
            Topic topic = new Topic();
            topic.setName(vo.getName() );
            topicNsql.setData(topic);
            if (vo.getId() != null)
                topicNsql.setId(vo.getId());
        }
        return topicNsql;
    }

}
