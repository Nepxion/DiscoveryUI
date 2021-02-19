import { Button, Result, Space, Radio, Modal, Divider, Row, Col, Typography, Menu, Dropdown, Tabs, Input } from 'antd';
import React, { useState } from 'react';
import { history } from 'umi';
import { CheckOutlined, DownOutlined } from '@ant-design/icons';
import { constant } from 'lodash';
const { Title, Text, Link } = Typography;

const serviceBlueGreenSave = () => {
 const [visible, setVisible] = useState(false);
 // 新建 - 确定
 const save = () => {

 }

 return (
  <>
     <Space>
      <Button icon={<CheckOutlined />}
       onClick={() => {
        save();
       }}
      >保存</Button>
     </Space>
  </>
 );
};

export default serviceBlueGreenSave;
