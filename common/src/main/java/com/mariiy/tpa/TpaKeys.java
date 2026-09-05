package com.mariiy.tpa;

/**
 * Translation keys for Mariiy-TPA. Values live in {@code assets/mariiy_tpa/lang/*.json}.
 */
public final class TpaKeys {
    public static final String PREFIX = "mariiy_tpa.prefix";
    public static final String ACCEPT = "mariiy_tpa.accept";
    public static final String DENY = "mariiy_tpa.deny";
    public static final String ACCEPT_HOVER = "mariiy_tpa.accept.hover";
    public static final String DENY_HOVER = "mariiy_tpa.deny.hover";
    public static final String ACTION_TO = "mariiy_tpa.action.to";
    public static final String ACTION_HERE = "mariiy_tpa.action.here";
    public static final String OTHER = "mariiy_tpa.other";

    public static final String ERROR_DISABLED = "mariiy_tpa.error.disabled";
    public static final String ERROR_SELF = "mariiy_tpa.error.self";
    public static final String ERROR_OFFLINE = "mariiy_tpa.error.offline";
    public static final String ERROR_COOLDOWN = "mariiy_tpa.error.cooldown";
    public static final String ERROR_HAS_OUTGOING = "mariiy_tpa.error.has_outgoing";
    public static final String ERROR_TARGET_BUSY = "mariiy_tpa.error.target_busy";
    public static final String ERROR_EXPIRED = "mariiy_tpa.error.expired";
    public static final String ERROR_REQUESTER_OFFLINE = "mariiy_tpa.error.requester_offline";
    public static final String ERROR_DENIED_BY = "mariiy_tpa.error.denied_by";

    public static final String INFO_NO_REQUEST = "mariiy_tpa.info.no_request";
    public static final String INFO_CANCELLED = "mariiy_tpa.info.cancelled";
    public static final String INFO_CANCELLED_BY = "mariiy_tpa.info.cancelled_by";
    public static final String INFO_PEER_OFFLINE = "mariiy_tpa.info.peer_offline";
    public static final String INFO_DENIED = "mariiy_tpa.info.denied";
    public static final String INFO_EXPIRED = "mariiy_tpa.info.expired";

    public static final String SUCCESS_SENT = "mariiy_tpa.success.sent";
    public static final String SUCCESS_ACCEPTED = "mariiy_tpa.success.accepted";
    public static final String SUCCESS_TELEPORTED_TO = "mariiy_tpa.success.teleported_to";
    public static final String SUCCESS_ARRIVED_HERE = "mariiy_tpa.success.arrived_here";
    public static final String SUCCESS_TELEPORTED_HERE = "mariiy_tpa.success.teleported_here";
    public static final String SUCCESS_ARRIVED_TO_YOU = "mariiy_tpa.success.arrived_to_you";

    public static final String INVITE_BODY = "mariiy_tpa.invite.body";

    public static final String CMD_PLAYERS_ONLY = "mariiy_tpa.cmd.players_only";
    public static final String CMD_NO_PERMISSION = "mariiy_tpa.cmd.no_permission";
    public static final String CMD_USAGE_TPA = "mariiy_tpa.cmd.usage_tpa";
    public static final String CMD_USAGE_TPAHERE = "mariiy_tpa.cmd.usage_tpahere";
    public static final String CMD_NOT_ONLINE = "mariiy_tpa.cmd.not_online";

    public static final String BACK_NONE = "mariiy_tpa.back.none";
    public static final String BACK_FAILED = "mariiy_tpa.back.failed";
    public static final String BACK_SUCCESS = "mariiy_tpa.back.success";

    private TpaKeys() {
    }

    public static String action(TpaKind kind) {
        return kind == TpaKind.TO ? ACTION_TO : ACTION_HERE;
    }
}
