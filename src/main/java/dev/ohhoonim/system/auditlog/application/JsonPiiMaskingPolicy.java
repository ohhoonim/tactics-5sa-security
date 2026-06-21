package dev.ohhoonim.system.auditlog.application;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;
import dev.ohhoonim.component.model.unit.Policy;
import dev.ohhoonim.system.auditlog.model.MaskingPolicy;
import dev.ohhoonim.system.auditlog.model.MaskingResult;
import tools.jackson.databind.JsonNode;
import tools.jackson.databind.ObjectMapper;
import tools.jackson.databind.node.ArrayNode;
import tools.jackson.databind.node.JsonNodeFactory;
import tools.jackson.databind.node.ObjectNode;

@Policy
public class JsonPiiMaskingPolicy implements MaskingPolicy {

    private final ObjectMapper objectMapper;
    private final JsonNodeFactory nodeFactory;
    private final Set<String> targetFields = Set.of(
        "password", "ssn", "socialsecuritynumber", "bankaccount", "mobileno"
    );

    public JsonPiiMaskingPolicy(ObjectMapper objectMapper) {
        this.objectMapper = objectMapper;
        this.nodeFactory = objectMapper.getNodeFactory(); // nodeFactory 주입
    }

    @Override
    public MaskingResult apply(String rawJson) {
        if (rawJson == null || rawJson.isBlank()) {
            return MaskingResult.empty(rawJson);
        }

        try {
            JsonNode root = objectMapper.readTree(rawJson);
            List<String> maskedFields = new ArrayList<>();
            
            JsonNode maskedRoot = maskRecursive(root, maskedFields);

            return new MaskingResult(objectMapper.writeValueAsString(maskedRoot), maskedFields);
        } catch (Exception e) {
            throw new RuntimeException("Jackson 3 PII 마스킹 처리 중 오류가 발생했습니다.", e);
        }
    }

    private JsonNode maskRecursive(JsonNode node, List<String> maskedFields) {
        // 1. 객체 노드 처리
        if (node instanceof ObjectNode objectNode) {
            ObjectNode newNode = objectMapper.createObjectNode();
            var it = objectNode.propertyNames().iterator();
            while (it.hasNext()) {
                String fieldName = it.next();
                JsonNode childNode = objectNode.get(fieldName);
                if (targetFields.contains(fieldName.toLowerCase())) {
                    newNode.set(fieldName, nodeFactory.stringNode("****"));
                    maskedFields.add(fieldName);
                } else {
                    newNode.set(fieldName, maskRecursive(childNode, maskedFields));
                }
            }
            return newNode;
        } 
        
        // 2. 배열 노드 처리
        if (node instanceof ArrayNode arrayNode) {
            ArrayNode newArray = objectMapper.createArrayNode();
            for (JsonNode element : arrayNode) {
                newArray.add(maskRecursive(element, maskedFields));
            }
            return newArray;
        }

        // 3. 리프 노드(값)는 그대로 반환
        return node;
    }
}