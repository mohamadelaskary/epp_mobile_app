package net.gbs.epp_project.Base

import android.app.Activity
import android.app.Application
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import net.gbs.epp_project.MainActivity.MainActivityViewModel
import net.gbs.epp_project.Ui.Audit.AuditList.AuditListViewModel
import net.gbs.epp_project.Ui.Audit.AuditedList.AuditedListViewModel
import net.gbs.epp_project.Ui.Audit.CycleCount.CycleCount.CycleCountViewModel
import net.gbs.epp_project.Ui.Audit.CycleCount.OnHand.OnHandViewModel
import net.gbs.epp_project.Ui.Audit.CycleCount.StartCycleCount.ByItem.StartCycleCountByItemViewModel
import net.gbs.epp_project.Ui.Audit.CycleCount.StartCycleCount.ByLocator.StartCycleCountByLocatorViewModel
import net.gbs.epp_project.Ui.Audit.FinishTracking.FinishTrackingAuditList.FinishTrackingAuditListViewModel
import net.gbs.epp_project.Ui.Audit.FinishTracking.StartFinishing.StartFinishTrackingViewModel
import net.gbs.epp_project.Ui.Audit.StartAudit.StartAuditViewModel
import net.gbs.epp_project.Ui.ContainersReceiving.AddNewTruck.AddNewTruckViewModel
import net.gbs.epp_project.Ui.ContainersReceiving.CustomerDataSearch.CustomerNameSearchViewModel
import net.gbs.epp_project.Ui.FinishedProductsItemInfo.FinishedProductsItemInfoViewModel
import net.gbs.epp_project.Ui.Gate.CheckIn.CheckIn.CheckInViewModel
import net.gbs.epp_project.Ui.Gate.CheckIn.TrucksList.CheckInTruckListFragment
import net.gbs.epp_project.Ui.Gate.CheckIn.TrucksList.CheckInTruckListViewModel
import net.gbs.epp_project.Ui.Gate.ConfirmArrival.ConfirmArrival.ConfirmArrivalViewModel
import net.gbs.epp_project.Ui.Gate.ConfirmArrival.TrucksList.TruckListViewModel
import net.gbs.epp_project.Ui.Gate.RegisterArrival.RegisterArrivalViewModel
import net.gbs.epp_project.Ui.Issue.EppOrganizations.IssueMenus.EppOrganizationsIssueViewModel
import net.gbs.epp_project.Ui.Issue.EppOrganizations.SpareParts.TransactSparePartsWorkOrderViewModel

import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.TransactMoveOrderViewModel
import net.gbs.epp_project.Ui.Issue.EppOrganizations.TransactMoveOrderChemicalFactory.TransactionHistory.TransactionHistoryViewModel
import net.gbs.epp_project.Ui.ItemInfo.ItemInfoViewModel
import net.gbs.epp_project.Ui.Receiving.EppOrganizations.EppOrganizationsReceivingViewModel
import net.gbs.epp_project.Ui.Receiving.EppOrganizations.Inspection.InspectionViewModel
import net.gbs.epp_project.Ui.Receiving.EppOrganizations.Inspection.PODetailsFragment.PODetailsViewModel
import net.gbs.epp_project.Ui.Receiving.EppOrganizations.Inspection.StartInspection.StartInspectionViewModel
import net.gbs.epp_project.Ui.Receiving.EppOrganizations.ItemInfo.POsInfoViewModel
import net.gbs.epp_project.Ui.Receiving.EppOrganizations.PutAway.PutAwayDetails.PutAwayDetailsViewModel
import net.gbs.epp_project.Ui.Receiving.EppOrganizations.PutAway.Deliver.PutAwayViewModel
import net.gbs.epp_project.Ui.Receiving.EppOrganizations.PutAway.Rejection.RejectionPutAwayViewModel
import net.gbs.epp_project.Ui.Receiving.EppOrganizations.PutAway.StartPutAway.StartPutAwayViewModel
import net.gbs.epp_project.Ui.Receiving.EppOrganizations.Receive.ReceivePO.ReceivePOViewModel
import net.gbs.epp_project.Ui.Receiving.EppOrganizations.Receive.StartReceiving.StartReceiveViewModel
import net.gbs.epp_project.Ui.Return.ReturnMenu.ReturnMenuViewModel
import net.gbs.epp_project.Ui.Return.ReturnToVendor.AddItemScreen.AddItemScreenViewModel
import net.gbs.epp_project.Ui.Return.ReturnToVendor.ReturnToVendorViewModel
import net.gbs.epp_project.Ui.Return.ReturnToWarehouse.ReturnToWarehouseViewModel
import net.gbs.epp_project.Ui.Return.ReturnToWarehouse.StartReturn.StartReturnViewModel
import net.gbs.epp_project.Ui.Return.ReturnToWip.ReturnToWipViewModel
import net.gbs.epp_project.Ui.SplashAndSignIn.SignInViewModel
import net.gbs.epp_project.Ui.SplashAndSignIn.Update.UpdateApkViewModel
import net.gbs.epp_project.Ui.Transfer.EppOrganizationsTransferViewModel
import net.gbs.epp_project.Ui.Transfer.StartTransfer.StartTransferViewModel
import java.lang.IllegalArgumentException

class BaseViewModelFactory (val application: Application,val activity: Activity): ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(SignInViewModel::class.java))
            return SignInViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(AuditListViewModel::class.java))
            return AuditListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartAuditViewModel::class.java))
            return StartAuditViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(AuditedListViewModel::class.java))
            return AuditedListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(FinishTrackingAuditListViewModel::class.java))
            return FinishTrackingAuditListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartFinishTrackingViewModel::class.java))
            return StartFinishTrackingViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(EppOrganizationsReceivingViewModel::class.java))
            return EppOrganizationsReceivingViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(EppOrganizationsIssueViewModel::class.java))
            return EppOrganizationsIssueViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(TransactMoveOrderViewModel::class.java))
            return TransactMoveOrderViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(TransactionHistoryViewModel::class.java))
            return TransactionHistoryViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(EppOrganizationsReceivingViewModel::class.java))
            return EppOrganizationsReceivingViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(PutAwayViewModel::class.java))
            return PutAwayViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(PutAwayDetailsViewModel::class.java))
            return PutAwayDetailsViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(ReturnMenuViewModel::class.java))
            return ReturnMenuViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(ReturnToVendorViewModel::class.java))
            return ReturnToVendorViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(ReturnToWarehouseViewModel::class.java))
            return ReturnToWarehouseViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartReturnViewModel::class.java))
            return StartReturnViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(ReceivePOViewModel::class.java))
            return ReceivePOViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartReceiveViewModel::class.java))
            return StartReceiveViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(InspectionViewModel::class.java))
            return InspectionViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartInspectionViewModel::class.java))
            return StartInspectionViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartPutAwayViewModel::class.java))
            return StartPutAwayViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(CycleCountViewModel::class.java))
            return CycleCountViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartCycleCountByItemViewModel::class.java))
            return StartCycleCountByItemViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartCycleCountByLocatorViewModel::class.java))
            return StartCycleCountByLocatorViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(OnHandViewModel::class.java))
            return OnHandViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(PODetailsViewModel::class.java))
            return PODetailsViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(POsInfoViewModel::class.java))
            return POsInfoViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(EppOrganizationsTransferViewModel::class.java))
            return EppOrganizationsTransferViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(StartTransferViewModel::class.java))
            return StartTransferViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(ItemInfoViewModel::class.java))
            return ItemInfoViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(MainActivityViewModel::class.java))
            return MainActivityViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(TransactSparePartsWorkOrderViewModel::class.java))
            return TransactSparePartsWorkOrderViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(ReturnToWipViewModel::class.java))
            return ReturnToWipViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(FinishedProductsItemInfoViewModel::class.java))
            return FinishedProductsItemInfoViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(CustomerNameSearchViewModel::class.java))
            return CustomerNameSearchViewModel(activity,application) as T
        else if (modelClass.isAssignableFrom(AddNewTruckViewModel::class.java))
            return AddNewTruckViewModel(activity,application) as T
        else if (modelClass.isAssignableFrom(RejectionPutAwayViewModel::class.java))
            return RejectionPutAwayViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(AddItemScreenViewModel::class.java))
            return AddItemScreenViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(TruckListViewModel::class.java))
            return TruckListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(ConfirmArrivalViewModel::class.java))
            return ConfirmArrivalViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(RegisterArrivalViewModel::class.java))
            return RegisterArrivalViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(CheckInViewModel::class.java))
        return CheckInViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(CheckInTruckListViewModel::class.java))
            return CheckInTruckListViewModel(application,activity) as T
        else if (modelClass.isAssignableFrom(UpdateApkViewModel::class.java))
            return UpdateApkViewModel(application,activity) as T
        throw IllegalArgumentException("View model not found")
    }
}