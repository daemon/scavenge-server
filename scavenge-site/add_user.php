<?php
chdir('/home/td/forum');
define('IN_PHPBB', true);
$phpEx = "php";
$phpbb_root_path = (defined('PHPBB_ROOT_PATH')) ? PHPBB_ROOT_PATH : './';
require($phpbb_root_path . 'common.' . $phpEx);
require($phpbb_root_path . 'includes/functions_user.' . $phpEx);
require($phpbb_root_path . 'includes/functions_module.' . $phpEx);
$user->session_begin();
$auth->acl($user->data);
$user->setup('ucp');
$username = $argv[1];
$password = $argv[2];
$email_address = $argv[3];
global $config, $db, $user, $auth, $template, $phpbb_root_path, $phpEx;

        $user_row = array(
            'username'                => $username,                //REQUIRED IN FORM
            'user_password'            => md5($password),            //REQUIRED IN FORM
            'user_email'            => $email_address,            //REQUIRED IN FORM
            'group_id'                =>    2,//(int) $group_id,
            'user_timezone'            => $timezone = date(Z) / 3600,//(float) $data[tz],
            'user_lang'                => $user->lang_name,//$data[lang],
            'user_type'                => USER_NORMAL,//$user_type,
            'user_actkey'            => '',//$user_actkey,
            'user_ip'                => $user->ip,
            'user_regdate'            => time(),
            'user_inactive_reason'    => 0,//$user_inactive_reason,
            'user_inactive_time'    => 0,//$user_inactive_time,
        );


    // Register user...
        $user_id = user_add($user_row);

?>
