import {
    CalendarDaysIcon,
    CheckBadgeIcon,
    EnvelopeIcon,
    FaceSmileIcon,
    IdentificationIcon,
    KeyIcon,
    LockClosedIcon,
    PhoneIcon,
    UserIcon,
} from "@heroicons/react/24/outline";
import { useEffect, useState, type SubmitEvent } from "react";
import { useNavigate, useParams } from "react-router-dom";
import type { ErrorResponse } from "../../api/api";
import ActionButton from "../../components/ActionButtonComponent";
import DividerComponent from "../../components/DividerComponent";
import InputComponent from "../../components/formelements/InputComponent";
import SelectComponent from "../../components/formelements/SelectComponent";
import SectionLayoutComponent from "../../components/layout/SectionLayoutComponent";
import Notification from "../../components/notifications/Notification";
import { useNotifications } from "../../components/notifications/useNotifications";
import useAuthContext, {
    UserGender,
    type UserProfile,
} from "../../context/useAuthContext";
import { RoutePaths } from "../../routes/RoutePaths";
import UserService from "../../services/UserService";

export default function ProfilePage() {
    const { userId } = useParams<{ userId: string }>();

    const { profile: user, isAdmin, updateProfile } = useAuthContext();
    const navigate = useNavigate();

    const [fetchedProfile, setFetchedUserProfile] =
        useState<UserProfile | null>(null);
    const userProfile = userId ? fetchedProfile : user;

    const { notifications, setNotifications } = useNotifications([
        "profile",
        "password",
    ]);
    const profileNotifications = notifications["profile"] ?? null;
    const passwordNotifications = notifications["password"] ?? null;

    const [actionProgress, setActionProgress] = useState<
        "profile" | "password" | null
    >(null);

    useEffect(() => {
        if (!userId) return;

        if (!isAdmin) {
            navigate(RoutePaths.MANAGE);
            return;
        }

        UserService.getUserById<UserProfile>(userId).then((resp) => {
            const prf = resp.data;
            setFetchedUserProfile({
                ...prf,
                name: `${prf.firstName} ${prf.lastName}`,
            });
        });
    }, [userId, isAdmin, navigate]);

    function handleProfileUpdate(e: SubmitEvent<HTMLFormElement>) {
        e.preventDefault();

        const formData = new FormData(e.currentTarget);

        const payload = {
            ...userProfile,
            name: formData.get("firstName") + " " + formData.get("lastName"),
            firstName: formData.get("firstName"),
            lastName: formData.get("lastName"),
            phone: formData.get("phone"),
            gender: formData.get("gender"),
            dob: formData.get("dob"),
            address: userProfile?.address,
        };

        setActionProgress("profile");
        UserService.updateProfile<UserProfile>(
            String(userId ?? user?.id),
            payload,
        )
            .then((resp) => {
                const prf = {
                    ...resp.data,
                    name: `${resp.data.firstName} ${resp.data.lastName}`,
                };
                setFetchedUserProfile(prf);
                updateProfile(prf);
                setNotifications("profile", {
                    type: "success",
                    messages: "Profile updated successfully",
                });
            })
            .catch((e: ErrorResponse) => {
                setNotifications("profile", {
                    type: "error",
                    messages: e,
                });
            })
            .finally(() => {
                setActionProgress(null);
            });
    }

    function handlePasswordChange(e: SubmitEvent<HTMLFormElement>) {
        e.preventDefault();

        const formData = new FormData(e.currentTarget);

        const confirmPassword = formData.get("confirmPassword");
        const newPassword = formData.get("newPassword");

        if (newPassword !== confirmPassword) {
            setNotifications("password", {
                type: "error",
                messages: "Passwords didn't matched!",
            });
            return;
        }

        const payload = {
            email: userProfile?.email,
            oldPassword: formData.get("oldPassword"),
            newPassword: formData.get("newPassword"),
        };
        setActionProgress("password");
        UserService.changePassword<UserProfile>(payload)
            .then(() => {
                setNotifications("password", {
                    type: "success",
                    messages: "Password updated successfully",
                });
            })
            .catch((e: ErrorResponse) => {
                setNotifications("password", {
                    type: "error",
                    messages: e,
                });
            })
            .finally(() => {
                setActionProgress(null);
            });
    }

    return (
        <SectionLayoutComponent
            title="Profile"
            description={`Manage ${isAdmin && fetchedProfile ? `${fetchedProfile.name}'s` : "your"} Profile details`}
        >
            <div className="flex flex-col md:flex-row flex-wrap gap-3 justify-center *:flex-1">
                <form
                    key={fetchedProfile?.id ?? "loading"}
                    onSubmit={handleProfileUpdate}
                    className="grid grid-cols-1 md:grid-cols-2 items-center gap-3 *:mb-0 p-3 dark:bg-background-dark border rounded"
                >
                    <DividerComponent
                        text="Edit Profile"
                        icon={IdentificationIcon}
                        className="col-span-full"
                    />
                    {profileNotifications && (
                        <div className="col-span-full">
                            <Notification
                                type={profileNotifications.type}
                                messages={profileNotifications.messages}
                            />
                        </div>
                    )}
                    <InputComponent
                        id="firstName"
                        placeholder="enter firstname"
                        label={{
                            icon: UserIcon,
                        }}
                        name="firstName"
                        defaultValue={userProfile?.firstName ?? ""}
                        customise="inherit-default"
                        required
                    />
                    <InputComponent
                        id="lastName"
                        label={{
                            icon: UserIcon,
                        }}
                        placeholder="enter lastname"
                        name="lastName"
                        defaultValue={userProfile?.lastName ?? ""}
                        customise="inherit-default"
                        required
                    />
                    <InputComponent
                        id="email"
                        type="email"
                        label={{
                            icon: EnvelopeIcon,
                        }}
                        placeholder="enter email"
                        name="email"
                        defaultValue={userProfile?.email ?? ""}
                        customise="inherit-default"
                        disabled
                        required
                    />
                    <InputComponent
                        id="phone"
                        label={{
                            icon: PhoneIcon,
                        }}
                        placeholder="enter phone"
                        name="phone"
                        defaultValue={userProfile?.phone ?? ""}
                        customise="inherit-default"
                        required
                    />
                    <InputComponent
                        id="date"
                        label={{
                            icon: CalendarDaysIcon,
                        }}
                        type="date"
                        placeholder="Date Of Birth"
                        name="dob"
                        defaultValue={userProfile?.dob ?? ""}
                        customise="inherit-default"
                        required
                    />
                    <SelectComponent
                        id="userGender"
                        label={{
                            icon: FaceSmileIcon,
                        }}
                        emptyOption="Gender"
                        options={Object.keys(UserGender).map((g) => ({
                            text: g,
                            value: g,
                        }))}
                        name="gender"
                        defaultValue={userProfile?.gender ?? ""}
                        customise="inherit-default"
                        required
                    />
                    <div className="col-span-full text-end">
                        <ActionButton
                            text="Update"
                            type="submit"
                            spinner={{
                                isLoading: actionProgress === "profile",
                            }}
                            disabled={actionProgress === "profile"}
                            icon={CheckBadgeIcon}
                            className="primary ml-auto"
                        />
                    </div>
                </form>
                {!userId && (
                    <form
                        onSubmit={handlePasswordChange}
                        className="grid grid-cols-1 gap-3 items-center p-3 dark:bg-background-dark border rounded"
                    >
                        <DividerComponent
                            text="Old Password"
                            icon={KeyIcon}
                            className="col-span-full"
                        />
                        {passwordNotifications && (
                            <div className="col-span-full">
                                <Notification
                                    type={passwordNotifications.type}
                                    messages={passwordNotifications.messages}
                                />
                            </div>
                        )}
                        <InputComponent
                            label={{
                                icon: KeyIcon,
                            }}
                            type="password"
                            id="currentPass"
                            placeholder="Authenticate"
                            name="oldPassword"
                            customise="inherit-default"
                            required
                        />
                        <div className=""></div>
                        <DividerComponent
                            text="new Password"
                            icon={LockClosedIcon}
                            className="col-span-full"
                        />
                        <InputComponent
                            label={{
                                icon: LockClosedIcon,
                            }}
                            type="password"
                            id="newPassword"
                            placeholder="New password"
                            name="newPassword"
                            onChange={(e) => {
                                const confirmPass = document.getElementById(
                                    "confirmPassword",
                                ) as HTMLInputElement;
                                if (
                                    e.target.value !== "" &&
                                    confirmPass.value !== e.target.value
                                ) {
                                    setNotifications(
                                        "password",
                                        {
                                            type: "error",
                                            messages: [
                                                "Two Passwords didn't matched!",
                                            ],
                                        },
                                        20,
                                    );
                                } else {
                                    setNotifications("password", null);
                                }
                            }}
                            customise="inherit-default"
                            required
                        />
                        <InputComponent
                            label={{
                                icon: LockClosedIcon,
                            }}
                            type="password"
                            id="confirmPassword"
                            placeholder="Confirm new password"
                            name="confirmPassword"
                            onChange={(e) => {
                                const newPass = document.getElementById(
                                    "newPassword",
                                ) as HTMLInputElement;
                                if (
                                    e.target.value !== "" &&
                                    newPass.value !== e.target.value
                                ) {
                                    setNotifications(
                                        "password",
                                        {
                                            type: "error",
                                            messages: [
                                                "Two Passwords didn't matched!",
                                            ],
                                        },
                                        20,
                                    );
                                } else {
                                    setNotifications("password", null);
                                }
                            }}
                            customise="inherit-default"
                            required
                        />
                        <div className="col-span-full">
                            <ActionButton
                                type="submit"
                                text="Change"
                                icon={KeyIcon}
                                className="primary ml-auto"
                                spinner={{
                                    isLoading: actionProgress === "password",
                                }}
                                disabled={actionProgress === "password"}
                            />
                        </div>
                    </form>
                )}
            </div>
        </SectionLayoutComponent>
    );
}
