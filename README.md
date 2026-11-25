# AdvancedPowerMenu (experimental)
Samsung Quick Panel tile and optional LSPosed power-menu entry. No kernel, boot image, system partition or init changes. The app performs the fixed KernelSU command only after explicit confirmation, unlocked/foreground checks and successful root/daemon preflight. No fallback to full reboot.

## Test in stages
1. Do not install on a phone with unstable root, bootloops or an untested recovery plan. Back up important data and know how to disable LSPosed before enabling.
2. Install the APK from Actions. Use Quick Panel edit to add Soft restart; LSPosed is not needed for this stage. Grant root only to this app via KernelSU. First open the confirmation screen and cancel; then test on a stable device after saving all work.
3. For the optional power menu: enable module in LSPosed, select ONLY System UI (com.android.systemui), and restart normally once. Test opening/closing the menu, original emergency controls, locking/unlocking, and then confirm restart only when ready.
4. If SystemUI misbehaves, disable this module using LSPosed or its documented rescue method; do not repeatedly soft restart. If root is unstable do not use it.
5. Android 16/17 Samsung firmware is the intended target; power menu layout and actual reboot behavior have NOT been validated on a physical phone. No promise of bootloop-free operation. No automatic commands or retries. The custom power-menu action uses the native restart icon and renderer, and is omitted if the expected interfaces are not present.

Source inspection: supplied SystemUI references SamsungGlobalActionsPresenter.getValidActions(), and the supplied framework defines that method, ActionViewModel and ActionInfo. The supplied KernelSU APK contains soft-reboot. Neither OEM nor KernelSU binaries are redistributed.
