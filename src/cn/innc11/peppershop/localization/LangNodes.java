package cn.innc11.peppershop.localization;

public enum LangNodes
{
	buy,
	sell,
	server_nickname,


	enchant_text,
	enchant_each_line,
	enchant_prefix,
	enchant_suffix,
	enchant_prefix_first,
	enchant_suffix_last,


	im_shop_type_updated,
	im_shop_type_donot_need_update,
	im_not_allow_modify_price_not_owner,
	im_not_selected_shop,
	im_intercept_console,
	im_shop_price_updated,
	im_price_wrong_format,
	im_price_wrong_args,
	im_shop_updated_server,
	im_shop_updated_ordinary,
	im_shop_info,
	im_enter_transactions_volume,
	im_not_a_number,
	im_successfully_removed_shop,
	im_not_allow_remove_shop_not_owner,
	im_creating_shop_enter_price,
	im_successfully_created_shop,
	im_no_item_in_hand,
	im_shop_sign_blocked,
	im_trade_canceled,
	im_buyshop_owner,
	im_buyshop_customer,
	im_buyshop_backpack_full,
	im_buyshop_sold_out,
	im_buyshop_insufficient_stock,
	im_buyshop_not_enough_money,
	im_sellshop_not_enough_item,
	im_sellshop_not_enough_money,
	im_sellshop_owner,
	im_sellshop_customer,
	im_sellshop_stock_full,
	im_shop_data_updated,
	im_no_residence_permission,
	im_create_shop_in_residence_only,
	im_not_allow_cross_residence,
	im_sign_not_in_residence,
	im_not_allow_sign_in_another_residence,
	im_not_allow_others_open_chest,
	im_not_allow_have_more_shop,


	pm_reload_done,
	pm_configure_updated,
	pm_help_player,
	pm_help_operator,
	pm_cannot_work_with_quickshop,
	pm_cannot_work_with_quickshopx,
	pm_linked_with_residence,
	pm_linked_with_land,
	pm_discovered_easyapi,
	pm_gac_warning_1,
	pm_gac_warning_2,
	pm_custom_name_file_found,
	pm_loaded_custom_item_names,
	pm_loaded_shops,

	shopdata_title,
	shopdata_price,
	shopdata_owner,
	shopdata_type,
	shopdata_server_shop,

	trading_title,
	trading_shop_info,
	trading_trading_volume,

	owner_title,
	owner_content,
	owner_button_shop_data_panel,
	owner_button_shop_trading_panel,

	cp_title,
	cp_interaction_time,
	cp_holographic_item,
	cp_interaction_way,
	cp_packet_send_ps,
	cp_link_with_residence,
	cp_link_with_land,
	cp_create_in_residence_only,
	cp_op_ignore_build_permission,
	cp_hopper_limit,
	cp_use_translated_item_name,
	cp_tax,


	buyshop_text1,
	buyshop_text2,
	buyshop_text3,
	buyshop_text4,
	buyshop_stock,

	sellshop_text1,
	sellshop_text2,
	sellshop_text3,
	sellshop_text4,
	sellshop_stock,


	E_0,
	E_1,
	E_2,
	E_3,
	E_4,
	E_5,
	E_6,
	E_7,
	E_8,
	E_9,
	E_10,
	E_11,
	E_12,
	E_13,
	E_14,
	E_15,
	E_16,
	E_17,
	E_18,
	E_19,
	E_20,
	E_21,
	E_22,
	E_23,
	E_24,
	E_25,
	E_26,
	E_27,
	E_28,
	E_29,
	E_30,
	E_31,
	E_32;

	public String  getDefaultLangText()
	{
		return name().replace("_", "-");
	}

	public static boolean contains(String value)
	{
		for(LangNodes k : values())
		{
			if(k.name().equals(value))
				return true;
		}

		return false;
	}
}
