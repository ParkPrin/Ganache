package me.parkprin.ganache.document.model;

public enum MenuType {
    Item("item"),   // 상품관리
    ItemReserve("itemreserve"), // 예약단가
    Cust("cust"),   // 거래처관리
    FindStockByItem("findStockByItem"),    // 상품별재고현황
    FindStockByWarehouse("findStockByWarehouse"),   // 재고처별재고현황
    AdjustStock("adjustStock");  // 실사재고조정

    final private String name;

    MenuType(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }
}
