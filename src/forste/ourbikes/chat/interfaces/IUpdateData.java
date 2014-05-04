package forste.ourbikes.chat.interfaces;
import forste.ourbikes.chat.types.FriendInfo;


public interface IUpdateData {
	public void updateData(FriendInfo[] friends, FriendInfo[] unApprovedFriends, String userKey);

}
