package com.wanyi.plugins.model;

import com.wanyi.plugins.enums.SocketMsgType;

//货物库存数量
public class CargoStockVo extends SocketBaseMsg{

    //取货码剩余数量
    private int codeLeftTotal;

    //货物剩余数量
    private int cargoLeftTotal;

    //货物已取数量
    private int cargoTookTotal;

    public CargoStockVo(int codeLeftTotal, int cargoLeftTotal, int cargoTookTotal) {
        this.codeLeftTotal = codeLeftTotal;
        this.cargoLeftTotal = cargoLeftTotal;
        this.cargoTookTotal = cargoTookTotal;
        super.type = SocketMsgType.CARGO_STOCK_VO;
    }

    public int getCodeLeftTotal() {
        return codeLeftTotal;
    }

    public void setCodeLeftTotal(int codeLeftTotal) {
        this.codeLeftTotal = codeLeftTotal;
    }

    public int getCargoLeftTotal() {
        return cargoLeftTotal;
    }

    public void setCargoLeftTotal(int cargoLeftTotal) {
        this.cargoLeftTotal = cargoLeftTotal;
    }

    public int getCargoTookTotal() {
        return cargoTookTotal;
    }

    public void setCargoTookTotal(int cargoTookTotal) {
        this.cargoTookTotal = cargoTookTotal;
    }
}
