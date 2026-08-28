package net.gbs.epp_project.Model

data class AuditLocator (
    var auditOrderList : MutableList<AuditOrderItemWithLocation>,
    var isFullScanned  : Boolean
)