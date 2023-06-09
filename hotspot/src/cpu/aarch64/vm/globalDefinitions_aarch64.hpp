/*
 * Copyright (c) 2013, Red Hat Inc.
 * Copyright (c) 1999, 2010, Oracle and/or its affiliates.
 * All rights reserved.
 * DO NOT ALTER OR REMOVE COPYRIGHT NOTICES OR THIS FILE HEADER.
 *
 * This code is free software; you can redistribute it and/or modify it
 * under the terms of the GNU General Public License version 2 only, as
 * published by the Free Software Foundation.
 *
 * This code is distributed in the hope that it will be useful, but WITHOUT
 * ANY WARRANTY; without even the implied warranty of MERCHANTABILITY or
 * FITNESS FOR A PARTICULAR PURPOSE.  See the GNU General Public License
 * version 2 for more details (a copy is included in the LICENSE file that
 * accompanied this code).
 *
 * You should have received a copy of the GNU General Public License version
 * 2 along with this work; if not, write to the Free Software Foundation,
 * Inc., 51 Franklin St, Fifth Floor, Boston, MA 02110-1301 USA.
 *
 * Please contact Oracle, 500 Oracle Parkway, Redwood Shores, CA 94065 USA
 * or visit www.oracle.com if you need additional information or have any
 * questions.
 *
 */

#ifndef CPU_AARCH64_VM_GLOBALDEFINITIONS_AARCH64_HPP
#define CPU_AARCH64_VM_GLOBALDEFINITIONS_AARCH64_HPP

const int StackAlignmentInBytes  = 16;

// Indicates whether the C calling conventions require that
// 32-bit integer argument values are properly extended to 64 bits.
// If set, SharedRuntime::c_calling_convention() must adapt
// signatures accordingly.
const bool CCallingConventionRequiresIntsAsLongs = false;

#define SUPPORTS_NATIVE_CX8

// The maximum B/BL offset range on AArch64 is 128MB.
#undef CODE_CACHE_DEFAULT_LIMIT
#define CODE_CACHE_DEFAULT_LIMIT (128*M)

#if defined(__APPLE__) || defined(_WIN64)
#define R18_RESERVED
#define R18_RESERVED_ONLY(code) code
#define NOT_R18_RESERVED(code)
#else
#define R18_RESERVED_ONLY(code)
#define NOT_R18_RESERVED(code) code
#endif

#endif // CPU_AARCH64_VM_GLOBALDEFINITIONS_AARCH64_HPP
