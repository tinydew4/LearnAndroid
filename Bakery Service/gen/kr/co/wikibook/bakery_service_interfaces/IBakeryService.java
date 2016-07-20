/*
 * This file is auto-generated.  DO NOT MODIFY.
 * Original file: /mnt/hgfs/arus/Documents/workspace/Bakery Service/src/kr/co/wikibook/bakery_service_interfaces/IBakeryService.aidl
 */
package kr.co.wikibook.bakery_service_interfaces;
public interface IBakeryService extends android.os.IInterface
{
/** Local-side IPC implementation stub class. */
public static abstract class Stub extends android.os.Binder implements kr.co.wikibook.bakery_service_interfaces.IBakeryService
{
private static final java.lang.String DESCRIPTOR = "kr.co.wikibook.bakery_service_interfaces.IBakeryService";
/** Construct the stub at attach it to the interface. */
public Stub()
{
this.attachInterface(this, DESCRIPTOR);
}
/**
 * Cast an IBinder object into an kr.co.wikibook.bakery_service_interfaces.IBakeryService interface,
 * generating a proxy if needed.
 */
public static kr.co.wikibook.bakery_service_interfaces.IBakeryService asInterface(android.os.IBinder obj)
{
if ((obj==null)) {
return null;
}
android.os.IInterface iin = (android.os.IInterface)obj.queryLocalInterface(DESCRIPTOR);
if (((iin!=null)&&(iin instanceof kr.co.wikibook.bakery_service_interfaces.IBakeryService))) {
return ((kr.co.wikibook.bakery_service_interfaces.IBakeryService)iin);
}
return new kr.co.wikibook.bakery_service_interfaces.IBakeryService.Stub.Proxy(obj);
}
public android.os.IBinder asBinder()
{
return this;
}
@Override public boolean onTransact(int code, android.os.Parcel data, android.os.Parcel reply, int flags) throws android.os.RemoteException
{
switch (code)
{
case INTERFACE_TRANSACTION:
{
reply.writeString(DESCRIPTOR);
return true;
}
case TRANSACTION_getBread:
{
data.enforceInterface(DESCRIPTOR);
android.content.ComponentName _arg0;
if ((0!=data.readInt())) {
_arg0 = android.content.ComponentName.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
java.lang.String _result = this.getBread(_arg0);
reply.writeNoException();
reply.writeString(_result);
return true;
}
case TRANSACTION_enterBakery:
{
data.enforceInterface(DESCRIPTOR);
android.content.ComponentName _arg0;
if ((0!=data.readInt())) {
_arg0 = android.content.ComponentName.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _result = this.enterBakery(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_leaveBakery:
{
data.enforceInterface(DESCRIPTOR);
android.content.ComponentName _arg0;
if ((0!=data.readInt())) {
_arg0 = android.content.ComponentName.CREATOR.createFromParcel(data);
}
else {
_arg0 = null;
}
boolean _result = this.leaveBakery(_arg0);
reply.writeNoException();
reply.writeInt(((_result)?(1):(0)));
return true;
}
case TRANSACTION_getCustomerCount:
{
data.enforceInterface(DESCRIPTOR);
int _result = this.getCustomerCount();
reply.writeNoException();
reply.writeInt(_result);
return true;
}
}
return super.onTransact(code, data, reply, flags);
}
private static class Proxy implements kr.co.wikibook.bakery_service_interfaces.IBakeryService
{
private android.os.IBinder mRemote;
Proxy(android.os.IBinder remote)
{
mRemote = remote;
}
public android.os.IBinder asBinder()
{
return mRemote;
}
public java.lang.String getInterfaceDescriptor()
{
return DESCRIPTOR;
}
public java.lang.String getBread(android.content.ComponentName cn) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
java.lang.String _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((cn!=null)) {
_data.writeInt(1);
cn.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_getBread, _data, _reply, 0);
_reply.readException();
_result = _reply.readString();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean enterBakery(android.content.ComponentName cn) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((cn!=null)) {
_data.writeInt(1);
cn.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_enterBakery, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public boolean leaveBakery(android.content.ComponentName cn) throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
boolean _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
if ((cn!=null)) {
_data.writeInt(1);
cn.writeToParcel(_data, 0);
}
else {
_data.writeInt(0);
}
mRemote.transact(Stub.TRANSACTION_leaveBakery, _data, _reply, 0);
_reply.readException();
_result = (0!=_reply.readInt());
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
public int getCustomerCount() throws android.os.RemoteException
{
android.os.Parcel _data = android.os.Parcel.obtain();
android.os.Parcel _reply = android.os.Parcel.obtain();
int _result;
try {
_data.writeInterfaceToken(DESCRIPTOR);
mRemote.transact(Stub.TRANSACTION_getCustomerCount, _data, _reply, 0);
_reply.readException();
_result = _reply.readInt();
}
finally {
_reply.recycle();
_data.recycle();
}
return _result;
}
}
static final int TRANSACTION_getBread = (android.os.IBinder.FIRST_CALL_TRANSACTION + 0);
static final int TRANSACTION_enterBakery = (android.os.IBinder.FIRST_CALL_TRANSACTION + 1);
static final int TRANSACTION_leaveBakery = (android.os.IBinder.FIRST_CALL_TRANSACTION + 2);
static final int TRANSACTION_getCustomerCount = (android.os.IBinder.FIRST_CALL_TRANSACTION + 3);
}
public java.lang.String getBread(android.content.ComponentName cn) throws android.os.RemoteException;
public boolean enterBakery(android.content.ComponentName cn) throws android.os.RemoteException;
public boolean leaveBakery(android.content.ComponentName cn) throws android.os.RemoteException;
public int getCustomerCount() throws android.os.RemoteException;
}
