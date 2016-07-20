package kr.co.wikibook.bakery_service_interfaces;

import android.content.ComponentName;

interface IBakeryService {
    String getBread(in ComponentName cn);
    boolean enterBakery(in ComponentName cn);
    boolean leaveBakery(in ComponentName cn);
    int getCustomerCount();
}
