# Checks dont use modality call in dont use modality mode.

## Noncompliant Code Example

Procedure NonComplaint(Parameters) Export
	DoMessageBox("Message");
EndProcedure


## Compliant Solution

Procedure Complaint(Parameters) Export
	ShowMessageBox("Message");
EndProcedure


## See

