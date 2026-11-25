# AdvancedPowerMenu (experimental)
Samsung Quick Panel tile and optional LSPosed power-menu entry. No kernel, boot image, system partition or init changes. The app performs the fixed KernelSU command only after explicit confirmation, unlocked/foreground checks and successful root/daemon preflight. No fallback to full reboot.

## Test in stages
1. Do not install on a phone with unstable root, bootloops or an untested recovery plan. Back up important data and know how to disable LSPosed before enabling.
2. Install the APK from Actions. Use Quick Panel edit to add Soft restart; LSPosed is not needed for this stage. Grant root only to this app via KernelSU. First open the confirmation screen and cancel; then test on a stable device after saving all work.
3. For the optional power menu: enable module in LSPosed, select ONLY System UI (com.android.systemui), and restart normally once. Test opening/closing the menu, original emergency controls, locking/unlocking, and then confirm restart only when ready.
4. If SystemUI misbehaves, disable this module using LSPosed or its documented rescue method; do not repeatedly soft restart. If root is unstable do not use it.
5. Android 16/17 Samsung firmware is the intended target; power menu layout and actual reboot behavior have NOT been validated on a physical phone. No promise of bootloop-free operation. No automatic commands or retries. The custom power-menu action uses the native restart icon and renderer, and is omitted if the expected interfaces are not present.

Source inspection: supplied SystemUI references SamsungGlobalActionsPresenter.getValidActions(), and the supplied framework defines that method, ActionViewModel and ActionInfo. The supplied KernelSU APK contains soft-reboot. Neither OEM nor KernelSU binaries are redistributed.

## 0.2 native confirmation
The power-menu entry uses Samsung presenter confirmAction and requires the same selected proxy on a distinct second tap. A permission-protected SystemUI handoff runs the existing root preflight; it rejects other callers and interrupted launches. The blue power icon is applied only to the custom entry. The Quick Panel retains its independent dialog. Grant root to the app first via KernelSU; a first-time authorization prompt may interrupt the native handoff. Native confirmation/animation and permissions require real device testing.

## 0.3 Soft Restart
Display name updated; package ID unchanged. A density-independent circular icon replaces the previous bitmap to avoid cropped artwork. Welcome screen and setup guide credit [s23_hype](https://t.me/s23_hype). The root check does not execute a restart. Existing two-tap confirmation and restart path are unchanged. Test on supported devices before public release.
