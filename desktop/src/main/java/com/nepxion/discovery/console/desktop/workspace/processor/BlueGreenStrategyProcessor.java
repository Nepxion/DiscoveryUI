package com.nepxion.discovery.console.desktop.workspace.processor;

/**
 * <p>Title: Nepxion Discovery</p>
 * <p>Description: Nepxion Discovery</p>
 * <p>Copyright: Copyright (c) 2017-2050</p>
 * <p>Company: Nepxion</p>
 * @author Haojun Ren
 * @version 1.0
 */

import twaver.TDataBox;

import java.util.List;

import org.apache.commons.lang3.StringUtils;

import com.nepxion.cots.twaver.element.TElementManager;
import com.nepxion.cots.twaver.element.TLink;
import com.nepxion.cots.twaver.element.TNode;
import com.nepxion.discovery.console.desktop.workspace.type.EscapeType;
import com.nepxion.discovery.console.desktop.workspace.type.LinkType;
import com.nepxion.discovery.console.desktop.workspace.type.NodeType;
import com.nepxion.discovery.console.desktop.workspace.type.ReleaseType;
import com.nepxion.discovery.console.desktop.workspace.type.StrategyType;
import com.nepxion.discovery.console.entity.Instance;

public class BlueGreenStrategyProcessor implements StrategyProcessor {
    @Override
    public TDataBox fromConfig() {
        return null;
    }

    @SuppressWarnings({ "unchecked", "incomplete-switch" })
    @Override
    public String toConfig(StrategyType strategyType, TDataBox dataBox) {
        if (TElementManager.getNodes(dataBox).size() <= 1) {
            return StringUtils.EMPTY;
        }

        String strategyValue = strategyType.toString();

        StringBuilder basicStrategyStringBuilder = new StringBuilder();
        StringBuilder blueStrategyStringBuilder = new StringBuilder();
        StringBuilder greenStrategyStringBuilder = new StringBuilder();
        List<TNode> nodes = TElementManager.getNodes(dataBox);
        for (int i = nodes.size() - 1; i >= 0; i--) {
            TNode node = nodes.get(i);
            Instance instance = (Instance) node.getUserObject();
            NodeType nodeType = (NodeType) node.getBusinessObject();
            String serviceId = instance.getServiceId();
            String metadata = instance.getMetadata().get(strategyValue);
            switch (nodeType) {
                case BLUE:
                    blueStrategyStringBuilder.append("\"" + serviceId + "\":\"" + metadata + "\", ");
                    break;
                case GREEN:
                    greenStrategyStringBuilder.append("\"" + serviceId + "\":\"" + metadata + "\", ");
                    break;
                case BASIC:
                    basicStrategyStringBuilder.append("\"" + serviceId + "\":\"" + metadata + "\", ");
                    break;
            }
        }
        String basicStrategy = basicStrategyStringBuilder.toString();
        basicStrategy = basicStrategy.substring(0, basicStrategy.length() - 2);
        String blueStrategy = blueStrategyStringBuilder.toString();
        blueStrategy = blueStrategy.substring(0, blueStrategy.length() - 2);
        String greenStrategy = greenStrategyStringBuilder.toString();
        greenStrategy = greenStrategy.substring(0, greenStrategy.length() - 2);

        String blueCondition = null;
        String greenCondition = null;
        List<TLink> links = TElementManager.getLinks(dataBox);
        for (int i = links.size() - 1; i >= 0; i--) {
            TLink link = links.get(i);
            LinkType linkType = (LinkType) link.getBusinessObject();
            switch (linkType) {
                case BLUE:
                    blueCondition = link.getUserObject().toString();
                    break;
                case GREEN:
                    greenCondition = link.getUserObject().toString();
                    break;
            }
        }

        StringBuilder strategyStringBuilder = new StringBuilder();
        strategyStringBuilder.append("<?xml version=\"1.0\" encoding=\"UTF-8\"?>\n");
        strategyStringBuilder.append("<rule>\n");
        strategyStringBuilder.append("    <strategy>\n");
        strategyStringBuilder.append("        <" + strategyValue + ">{" + basicStrategy + "}</" + strategyValue + ">\n");
        strategyStringBuilder.append("    </strategy>\n\n");
        strategyStringBuilder.append("    <strategy-customization>\n");
        strategyStringBuilder.append("        <conditions type=\"" + ReleaseType.BLUE_GREEN.toString() + "\">\n");
        strategyStringBuilder.append("            <condition id=\"blue-condition\" header=\"" + EscapeType.escape(blueCondition) + "\" " + strategyValue + "-id=\"blue-" + strategyValue + "-route\"/>\n");
        strategyStringBuilder.append("            <condition id=\"green-condition\" header=\"" + EscapeType.escape(greenCondition) + "\" " + strategyValue + "-id=\"green-" + strategyValue + "-route\"/>\n");
        strategyStringBuilder.append("        </conditions>\n\n");
        strategyStringBuilder.append("        <routes>\n");
        strategyStringBuilder.append("            <route id=\"blue-" + strategyValue + "-route\" type=\"" + strategyValue + "\">{" + blueStrategy + "}</route>\n");
        strategyStringBuilder.append("            <route id=\"green-" + strategyValue + "-route\" type=\"" + strategyValue + "\">{" + greenStrategy + "}</route>\n");
        strategyStringBuilder.append("        </routes>\n");
        strategyStringBuilder.append("    </strategy-customization>\n");
        strategyStringBuilder.append("</rule>");

        return strategyStringBuilder.toString();
    }
}