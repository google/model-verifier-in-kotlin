# Copyright 2023 Google LLC
#
# Licensed under the Apache License, Version 2.0 (the "License");
# you may not use this file except in compliance with the License.
# You may obtain a copy of the License at
#
#     http://www.apache.org/licenses/LICENSE-2.0
#
# Unless required by applicable law or agreed to in writing, software
# distributed under the License is distributed on an "AS IS" BASIS,
# WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
# See the License for the specific language governing permissions and
# limitations under the License.

load("@io_bazel_rules_kotlin//kotlin:jvm.bzl", "kt_jvm_library", "kt_jvm_test")

kt_jvm_library(
    name = "lib",
    srcs = [
        "DatabaseTable.kt",
        "Lists.kt",
        "Sets.kt",
    ],
    visibility = ["//visibility:public"],
    deps = [],
)

kt_jvm_test(
    name = "ListsTest",
    srcs = ["ListsTest.kt"],
    test_class = "com.github.zmatti.mvik.lib.ListsTest",
    deps = [
        ":lib",
        "@maven//:junit_junit",
        "@maven//:com_google_truth_truth",
    ]
)

kt_jvm_test(
    name = "SetsTest",
    srcs = ["SetsTest.kt"],
    test_class = "com.github.zmatti.mvik.lib.SetsTest",
    deps = [
        ":lib",
        "@maven//:junit_junit",
        "@maven//:com_google_truth_truth",
    ]
)

kt_jvm_test(
    name = "DatabaseTableTest",
    srcs = ["DatabaseTableTest.kt"],
    test_class = "com.github.zmatti.mvik.lib.DatabaseTableTest",
    deps = [
        ":lib",
        "@maven//:junit_junit",
        "@maven//:com_google_truth_truth",
    ]
)
