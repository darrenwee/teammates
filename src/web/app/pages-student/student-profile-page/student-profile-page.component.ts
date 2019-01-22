import { Component, OnInit } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { HttpRequestService } from "../../../services/http-request.service";
import { NgbModal } from '@ng-bootstrap/ng-bootstrap';
import { AuthService } from "../../../services/auth.service";
import { AuthInfo } from "../../auth-info";
import { FormControl, FormGroup } from "@angular/forms"
import { StatusMessageService } from "../../../services/status-message.service";
import { ErrorMessageOutput } from "../../message-output";
import { NationalityData, NationalityService } from "../../../services/nationality.service";

interface StudentProfile {
  shortName: string;
  email: string;
  institute: string;
  nationality: string;
  gender: string;
  moreInfo: string;
  pictureKey: string;
}

interface StudentDetails {
  studentProfile: StudentProfile;
  requestId: string;
}

/**
 * Student profile page.
 */
@Component({
  selector: 'tm-student-profile-page',
  templateUrl: './student-profile-page.component.html',
  styleUrls: ['./student-profile-page.component.scss'],
})
export class StudentProfilePageComponent implements OnInit {

  user: string = '';
  student?: StudentDetails;
  editForm!: FormGroup;
  nationalities?: string[];

  constructor(private route: ActivatedRoute,
              private modalService: NgbModal,
              private requestService: HttpRequestService,
              private authService: AuthService,
              private nationalityService: NationalityService,
              private statusMessageService: StatusMessageService) {}

  ngOnInit(): void {
    // populate drop-down menu for nationality list
    this.initNationalities();

    this.route.queryParams.subscribe((queryParams: any) => {
      this.user = queryParams.user;
      this.loadStudentProfile();
    });
  }

  initNationalities(): void {
    this.nationalityService.getNationalities().subscribe((response: NationalityData) => {
      this.nationalities = response.nationalities;
    });
  }

  loadStudentProfile(): void {
    this.authService.getAuthUser().subscribe((auth: AuthInfo) => {
      if (auth.user) {
        this.user = auth.user.id;
        const paramMap: { [key: string]: string } = {
          googleid: auth.user.id,
        };

        // retrieve profile once we have a googleId
        this.requestService.get('/students', paramMap).subscribe((response: StudentDetails) => {
          if (response) {
            this.student = response;
            this.initStudentProfileForm(this.student.studentProfile);
          } else {
            this.statusMessageService.showErrorMessage('Error retrieving student profile');
          }
        }, (response: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(response.error.message);
        })
      }
    });
  }

  initStudentProfileForm(profile: StudentProfile): void {
    this.editForm = new FormGroup({
      shortName: new FormControl(profile.shortName),
      email: new FormControl(profile.email),
      institute: new FormControl(profile.institute),
      nationality: new FormControl(profile.nationality),
      gender: new FormControl(profile.gender),
      moreInfo: new FormControl(profile.moreInfo),
    })
  }

  onSubmit(confirmEditProfile: any) {
    this.modalService.open(confirmEditProfile);
  }

  submitEditForm(): void {
    console.log(<StudentProfile> this.editForm.value);
    const paramMap: { [key: string]: string } = {
      googleid: this.user,
    };

    this.requestService.put('/students', paramMap, <StudentProfile> this.editForm.value)
        .subscribe((response) => {
          if (response) {
            this.statusMessageService.showSuccessMessage('Successfully saved your profile')
          }
        }, (response: ErrorMessageOutput) => {
          this.statusMessageService.showErrorMessage(`Could not save your profile! ${response.error.message}`)
        })
  }
}
