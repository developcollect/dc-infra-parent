package com.developcollect.spring.json.anno;

import com.developcollect.core.utils.DesensitizedUtil;
import com.fasterxml.jackson.core.JsonGenerator;
import com.fasterxml.jackson.databind.BeanProperty;
import com.fasterxml.jackson.databind.JsonMappingException;
import com.fasterxml.jackson.databind.JsonSerializer;
import com.fasterxml.jackson.databind.SerializerProvider;
import com.fasterxml.jackson.databind.ser.ContextualSerializer;

import java.io.IOException;
import java.util.Objects;

public class DesensitizeSerializer extends JsonSerializer<String> implements ContextualSerializer {

    private DesensitizeType type;

    /**
     * 无参构造器不能去除，否则被spring加载到容器时无法创建对象
     */
    public DesensitizeSerializer() {
    }

    public DesensitizeSerializer(final DesensitizeType type) {
        this.type = type;
    }

    @Override
    public void serialize(final String s, final JsonGenerator jsonGenerator, final SerializerProvider serializerProvider) throws IOException {
        switch (this.type) {
            case CHINESE_NAME: {
                jsonGenerator.writeString(DesensitizedUtil.chineseName(s));
                break;
            }
            case ID_CARD: {
                jsonGenerator.writeString(DesensitizedUtil.idCardNum(s));
                break;
            }
            case FIXED_PHONE: {
                jsonGenerator.writeString(DesensitizedUtil.fixedPhone(s));
                break;
            }
            case MOBILE_PHONE: {
                jsonGenerator.writeString(DesensitizedUtil.desensitized(s, 2, 3));
                break;
            }
            case ADDRESS: {
                jsonGenerator.writeString(DesensitizedUtil.address(s, 8));
                break;
            }
            case EMAIL: {
                jsonGenerator.writeString(DesensitizedUtil.email(s));
                break;
            }
            case BANK_CARD: {
                jsonGenerator.writeString(DesensitizedUtil.bankCard(s));
                break;
            }
            case CNAPS_CODE: {
                jsonGenerator.writeString(DesensitizedUtil.cnapsCode(s));
                break;
            }
            case PASSWORD:
                jsonGenerator.writeString(DesensitizedUtil.password(s));
                break;
            case CAR_LICENSE:
                jsonGenerator.writeString(DesensitizedUtil.carLicense(s));
                break;
        }

    }

    @Override
    public JsonSerializer<?> createContextual(final SerializerProvider serializerProvider, final BeanProperty beanProperty) throws JsonMappingException {
        // 为空直接跳过
        if (beanProperty == null) {
            return serializerProvider.findNullValueSerializer(null);
        }

        // 如果是 String 类，尝试获取注解
        if (Objects.equals(beanProperty.getType().getRawClass(), String.class)) {
            Desensitize desensitize = beanProperty.getAnnotation(Desensitize.class);
            if (desensitize == null) {
                desensitize = beanProperty.getContextAnnotation(Desensitize.class);
            }

            // 如果能得到注解，就将注解的 value 传入 SensitiveInfoSerialize
            if (desensitize != null) {
                return new DesensitizeSerializer(desensitize.value());
            }
        }

        return serializerProvider.findValueSerializer(beanProperty.getType(), beanProperty);
    }
}