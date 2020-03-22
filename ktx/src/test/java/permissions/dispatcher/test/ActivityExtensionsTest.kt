package permissions.dispatcher.test

import android.Manifest
import android.app.Activity
import androidx.fragment.app.FragmentActivity
import com.nhaarman.mockitokotlin2.any
import com.nhaarman.mockitokotlin2.mock
import com.nhaarman.mockitokotlin2.verify
import io.mockk.every
import io.mockk.mockkObject
import io.mockk.mockkStatic
import io.mockk.unmockkAll
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.mockito.Mockito.never
import permissions.dispatcher.PermissionUtils
import permissions.dispatcher.ktx.Func
import permissions.dispatcher.ktx.PermissionRequestType
import permissions.dispatcher.ktx.ShowRationaleFunc
import permissions.dispatcher.ktx.withPermissionsCheck

class ActivityExtensionsTest {
    private lateinit var activity: FragmentActivity
    private lateinit var onShowRationale: ShowRationaleFunc
    private lateinit var onPermissionDenied: Func
    private lateinit var onNeverAskAgain: Func
    private lateinit var requiresPermission: Func

    @Before
    fun setUp() {
        mockkObject(PermissionRequestType.Others)

        activity = FragmentActivity()
        onShowRationale = mock()
        onPermissionDenied = mock()
        onNeverAskAgain = mock()
        requiresPermission = mock()
    }

    @After
    fun tearDown() = unmockkAll()

    @Test
    fun `requiresPermission is called when checkSelfPermission returns true`() {
        every { PermissionRequestType.Others.checkPermissions(any(), any()) } returns true

        activity.withPermissionsCheck(
            Manifest.permission.READ_CALENDAR,
            onPermissionDenied = onPermissionDenied,
            onNeverAskAgain = onNeverAskAgain,
            onShowRationale = onShowRationale,
            requiresPermission = requiresPermission
        )

        verify(onPermissionDenied, never()).invoke()
        verify(onNeverAskAgain, never()).invoke()
        verify(onShowRationale, never()).invoke(any())
        verify(requiresPermission).invoke()
    }

    @Test
    fun `onShowRationale is called when shouldShowRequestPermissionRationale returns true`() {
        every { PermissionRequestType.Others.checkPermissions(any(), any()) } returns false
        mockkStatic(PermissionUtils::class)
        every {
            PermissionUtils.shouldShowRequestPermissionRationale(any<Activity>(), any())
        } returns true

        activity.withPermissionsCheck(
            Manifest.permission.READ_CALENDAR,
            onPermissionDenied = onPermissionDenied,
            onNeverAskAgain = onNeverAskAgain,
            onShowRationale = onShowRationale,
            requiresPermission = requiresPermission
        )

        verify(onPermissionDenied, never()).invoke()
        verify(onNeverAskAgain, never()).invoke()
        verify(onShowRationale).invoke(any())
        verify(requiresPermission, never()).invoke()
    }

    @Test
    fun `no callback is called when requestPermissions is called`() {
        every { PermissionRequestType.Others.checkPermissions(any(), any()) } returns false
        every {
            PermissionRequestType.Others.requestPermissions(any(), any(), any(), any(), any()
            )
        } returns Unit

        activity.withPermissionsCheck(
            Manifest.permission.READ_CALENDAR,
            onPermissionDenied = onPermissionDenied,
            onNeverAskAgain = onNeverAskAgain,
            onShowRationale = onShowRationale,
            requiresPermission = requiresPermission
        )

        verify(onPermissionDenied, never()).invoke()
        verify(onNeverAskAgain, never()).invoke()
        verify(onShowRationale, never()).invoke(any())
        verify(requiresPermission, never()).invoke()
        io.mockk.verify { PermissionRequestType.Others.requestPermissions(
            arrayOf(Manifest.permission.READ_CALENDAR), activity,
            requiresPermission, onNeverAskAgain, onPermissionDenied) }
    }
}
