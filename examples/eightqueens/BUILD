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
    name = "eight_queens_model_lib",
    srcs = [
        "EightQueensModel.kt",
    ],
    deps = [
        "//framework",
        "//lang",
    ],
)

kt_jvm_test(
    name = "EightQueensModelTest",
    srcs = ["EightQueensModelTest.kt"],
    test_class = "com.google.mvik.examples.eightqueens.EightQueensModelTest",
    deps = [
        ":eight_queens_model_lib",
        "//testing",
        "@maven//:junit_junit",
        "@maven//:com_google_truth_truth",
        "@maven//:org_jetbrains_kotlin_kotlin_reflect",
    ]
)
