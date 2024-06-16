/*
 * Created 2017-3-3 11:47:10
 */
package cn.com.yting.kxy.core.wordfilter;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 *
 * @author Azige
 */
public class Trie{

    private final Node root;

    private Trie(Node root){
        this.root = root;
    }

    public static Trie create(List<? extends CharSequence> words){
        Node root = new Node();
        for (CharSequence word : words){
            root = addWordToNode(root, word);
        }
        return new Trie(root);
    }

    public List<SearchResult> search(CharSequence text){
        List<SearchResult> results = new ArrayList<>();
        for (int i = 0; i < text.length(); i++){
            char c = text.charAt(i);
            if (root.tail.length() > 0 && root.tail.charAt(0) == c || root.childrenMap.containsKey(c)){
                SearchResult result = tryMatch(text, i);
                if (result != null){
                    results.add(result);
                    i += result.getWord().length() - 1;
                }
            }
        }
        return results;
    }

    private SearchResult tryMatch(CharSequence text, int index){
        StringBuilder sb = new StringBuilder();
        String lastMatch = null;
        boolean matched = false;
        Node currentNode = root;
        int cursor = index;
        while (true){
            int subEnd = cursor + currentNode.tail.length();
            if (subEnd > text.length()){
                break;
            }
            CharSequence sub = text.subSequence(cursor, subEnd);
            if (currentNode.tail.equals(sub)){
                sb.append(sub);
                if (currentNode.end){
                    matched = true;
                    lastMatch = sb.toString();
                }
                if (subEnd >= text.length()){
                    break;
                }
                char lead = text.charAt(subEnd);
                if (currentNode.childrenMap.containsKey(lead)){
                    sb.append(lead);
                    currentNode = currentNode.childrenMap.get(lead);
                    cursor = subEnd + 1;
                }else{
                    break;
                }
            }else{
                break;
            }
        }
        if (matched){
            return new SearchResult(lastMatch, index);
        }else{
            return null;
        }
    }

    @Override
    public String toString(){
        return "Trie\n" + root.toString();
    }

    private static Node addWordToNode(Node node, CharSequence word){
        if (node.tail.equals("") && node.childrenMap.isEmpty()){
            node.tail = String.valueOf(word);
            node.end = true;
            return node;
        }else{
            int index = 0;
            for (;index < node.tail.length() && index < word.length(); index++){
                if (node.tail.charAt(index) != word.charAt(index)){
                    break;
                }
            }
            if (index == node.tail.length()){
                if (index == word.length()){
                    node.end = true;
                    return node;
                }
                char lead = word.charAt(index);
                CharSequence tail = word.subSequence(index + 1, word.length());
                Node childNode = node.childrenMap.get(lead);
                if (childNode == null){
                    childNode= new Node();
                }
                node.childrenMap.put(lead, addWordToNode(childNode, tail));
                return node;
            }else{
                Node newNode = new Node();
                newNode.tail = String.valueOf(word.subSequence(0, index));
                newNode.childrenMap.put(node.tail.charAt(index), node);
                node.tail = node.tail.substring(index + 1, node.tail.length());

                if (index == word.length()){
                    newNode.end = true;
                }else{
                    char lead = word.charAt(index);
                    CharSequence tail = word.subSequence(index + 1, word.length());
                    newNode.childrenMap.put(lead, addWordToNode(new Node(), tail));
                }
                return newNode;
            }
        }
    }

    private static class Node{

        String tail = "";
        Map<Character, Node> childrenMap = new HashMap<>(1);
        boolean end = false;

        private void buildString(StringBuilder sb, int depth){
            char[] prefix = new char[depth * 2];
            Arrays.fill(prefix, ' ');
            sb.append(tail).append("\n");
            if (end){
                sb.append(prefix).append("+-√").append("\n");
            }
            childrenMap.entrySet().forEach(entry -> {
                sb.append(prefix).append("+-").append(entry.getKey());
                entry.getValue().buildString(sb, depth + 1);
            });
        }

        @Override
        public String toString(){
            StringBuilder sb = new StringBuilder();
            buildString(sb, 0);
            return sb.toString();
        }
    }

}
