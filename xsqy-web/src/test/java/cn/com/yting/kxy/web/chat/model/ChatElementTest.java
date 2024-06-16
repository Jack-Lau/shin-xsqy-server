/*
 * Created 2018-7-30 18:02:56
 */
package cn.com.yting.kxy.web.chat.model;

import static org.hamcrest.Matchers.*;
import static org.junit.Assert.assertThat;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.SerializationFeature;
import com.google.common.collect.ImmutableMap;
import org.junit.After;
import org.junit.AfterClass;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;

/**
 *
 * @author Azige
 */
public class ChatElementTest {

    public ChatElementTest() {
    }

    @BeforeClass
    public static void setUpClass() {
    }

    @AfterClass
    public static void tearDownClass() {
    }

    @Before
    public void setUp() {
    }

    @After
    public void tearDown() {
    }

    @Test
    public void testSerialization() throws JsonProcessingException, IOException {
        ObjectMapper objectMapper = new ObjectMapper();
        objectMapper.configure(SerializationFeature.INDENT_OUTPUT, true);
        List<ChatElement<?>> list = Arrays.asList(
            new TextElement("asdasd"),
            new EmoticonElement(233),
            new TemplateElement(1, ImmutableMap.of("name", "asd", "age", "123"))
        );
        String json = objectMapper.writeValueAsString(list);
        System.out.println(json);
        List<ChatElement<?>> deserializedList = objectMapper.readValue(json, objectMapper.getTypeFactory().constructCollectionType(List.class, ChatElement.class));
        assertThat(deserializedList, is(equalTo(list)));
    }

}
