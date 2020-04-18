package com.may.routeplansystem.dao;

import com.may.routeplansystem.entity.po.NodePojo;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.junit4.SpringRunner;
import org.springframework.transaction.annotation.Transactional;

import javax.annotation.Resource;

import java.util.List;

import static org.junit.Assert.*;

@Transactional
@RunWith(SpringRunner.class)
@SpringBootTest
@ActiveProfiles("dev")
public class NodeDaoTest {

    @Resource
    private NodeDao nodeDao;

    @Test
    public void insertNode() {
        NodePojo node = new NodePojo();
        node.setNodeAddress("fds");
        node.setNodeName("fds");
        node.setLat(12);
        node.setLng(34);
        node.setIsCenter(0);
        node.setQuestionId(1);
        boolean flag = nodeDao.insertNode(node);
        assertTrue(flag);
    }

    @Test
    public void deleteNodeByNodeId() {
        boolean flag = nodeDao.deleteNodeByNodeId(1);
        assertTrue(flag);
    }

    @Test
    public void deleteNodeByQuestionId() {
        boolean flag = nodeDao.deleteNodeByNodeId(1);
        assertTrue(flag);
    }

    @Test
    public void deleteNodeByNodeName() {
        boolean flag = nodeDao.deleteNodeByNodeName("chongqing");
        assertTrue(flag);
    }

    @Test
    public void updateNodeByNodeId() {
        NodePojo node = new NodePojo();
        node.setNodeAddress("fds");
        node.setNodeName("fds");
        node.setLat(12);
        node.setLng(34);
        node.setIsCenter(0);
        node.setQuestionId(1);
        node.setNodeId(1);
        boolean flag = nodeDao.updateNodeByNodeId(node);
        assertTrue(flag);
    }

    @Test
    public void selectNodeByNodeId() {
        NodePojo node = nodeDao.selectNodeByNodeId(1);
        assertEquals("chongqing", node.getNodeName());
    }

    @Test
    public void selectNodeByNodeName() {
        NodePojo node = nodeDao.selectNodeByNodeName("chongqing");
        assertEquals("chongqing", node.getNodeAddress());
    }

    @Test
    public void selectCenterNode() {
        List<NodePojo> nodePojos = nodeDao.selectCenterNode(1);
        assertEquals(0, nodePojos.size());
    }

    @Test
    public void selectServiceNode() {
        List<NodePojo> nodePojos = nodeDao.selectServiceNode(1);
        assertEquals(2, nodePojos.size());
    }

    @Test
    public void selectAllNodes() {
        List<NodePojo> nodePojos = nodeDao.selectAllNodes(1);
        assertEquals(2, nodePojos.size());
    }

}