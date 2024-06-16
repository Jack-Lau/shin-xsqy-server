/*
 * Created 2017-3-7 11:25:50
 */
package cn.com.yting.kxy.core.wordfilter;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ForbiddenWordsChecker {

    private static final String DEFAULT_RESOURCE_NAME = "io/github/azige/mgxy/wordfilter/屏蔽词列表.csv";
    private final Trie trie;

    public ForbiddenWordsChecker() throws IOException {
        this(DEFAULT_RESOURCE_NAME);
    }

    public ForbiddenWordsChecker(String resourceName) throws IOException {
        try (InputStream input = Thread.currentThread().getContextClassLoader().getResourceAsStream(resourceName)) {
            if (input == null) {
                throw new IllegalArgumentException("不存在的资源：" + resourceName);
            } else {
                Scanner scanner = new Scanner(input, "UTF-8");
                List<String> words = new ArrayList<>();
                while (scanner.hasNextLine()) {
                    words.add(scanner.nextLine());
                }
                trie = Trie.create(words);
            }
        }
    }

    public ForbiddenWordsChecker(List<String> words) {
        trie = Trie.create(words);
    }

    /**
     * 检查给定字符串中是否存在屏蔽词
     *
     * @param text
     * @return
     */
    public boolean check(CharSequence text) {
        return !searchWords(text).isEmpty();
    }

    /**
     * 从给定字符串中检索屏蔽词
     *
     * @param text
     * @return 检索的结果的集合，可能为空集合，不会为 null
     */
    public List<SearchResult> searchWords(CharSequence text) {
        return trie.search(text);
    }
}
